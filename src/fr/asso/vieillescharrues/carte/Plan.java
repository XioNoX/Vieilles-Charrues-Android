/*
 * This file is part of Vieilles Charrues 2010.
 *
 * Vieilles Charrues 2010 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vieilles Charrues 2010 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Vieilles Charrues 2010.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.asso.vieillescharrues.carte;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.db.Tente;

/**
 * Classe gérant l'activité Plan
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Plan extends Activity implements LocationListener {

	private LocationManager myManager;

	private Location gps = new Location("gps");
	private PointF hautGauche; 
	private PointF hautDroite;  
	private PointF basGauche; 

	private PointF pointCarte;

	private int carteWidth;
	private int carteHeight;

	private DB maDB;
	private List<Tente> tentes;

	RelativeLayout rlPlan;

	ImageView ivPlan;
	ImageView ivPosition;

	Button btnCarte1;
	Button btnCarte2;
	TextView tvPrecision;

	LinearLayout llInfoBulle;
	TextView tvNomTente;
	TextView tvDistanceTente;
	Location locationTente = new Location("Tente");
	boolean infobulle = false;

	int idTenteCarte = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.carte );

		Bundle extras = getIntent().getExtras();
		if (extras != null) idTenteCarte = extras.getInt("idTente");

		this.myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		this.rlPlan = (RelativeLayout) findViewById(R.id.rlPlan);
		this.ivPlan = (ImageView) findViewById(R.id.ivPlan);
		ivPlan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (infobulle == true) {
					rlPlan.removeView(llInfoBulle);
					infobulle = false;
				}

			}
		});

		this.ivPosition = new ImageView(this);
		this.ivPosition.setImageDrawable(getResources().getDrawable(android.R.drawable.presence_online));

		maDB = new DB(this);

		tvPrecision = (TextView) findViewById(R.id.tvPrecision);

		btnCarte1 = (Button) findViewById(R.id.btnCarte1);
		btnCarte2 = (Button) findViewById(R.id.btnCarte2);

		btnCarte1.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) { 
				choixPlan(1, gps); 
			}
		});

		btnCarte2.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) { 
				choixPlan(2, gps);
			}
		});

		llInfoBulle = new LinearLayout(this);
		llInfoBulle.setBackgroundDrawable(getResources().getDrawable(R.drawable.infobulle));
		llInfoBulle.setOrientation(1);

		tvNomTente = new TextView(this);
		tvNomTente.setTextColor(Color.BLACK);
		llInfoBulle.addView(tvNomTente);

		tvDistanceTente = new TextView(this);
		tvDistanceTente.setTextColor(Color.BLACK);         

		llInfoBulle.addView(tvDistanceTente);	

		//Si l'on vient de l'activité tente, affichage de la carte2
		if (idTenteCarte == -1) choixPlan(1, null);
		else choixPlan(2, null);
	}

	/**
	 * Détecte un changement de position GPS
	 */
	@Override
	public void onLocationChanged(Location location) {
		//Affichage de la nouvelle position GPS
		affichagePosition(location);

		//Si une infobulle est ouverte, mise à jour de la distance de la tente
		if (infobulle == true)
			tvDistanceTente.setText(distanceTente());
	}

	/**
	 * Arrêt du GPS à la destruction de l'activité
	 */
	@Override
	protected void onDestroy() {
		stopListening();
		super.onDestroy();
	}

	/**
	 * Arrêt du GPS à la mise en pause de l'activité
	 */
	@Override
	protected void onPause() {
		stopListening();
		super.onPause();
	}

	/**
	 * Démarrage du GPS au retour à l'activité
	 */
	@Override
	protected void onResume() {
		startListening();
		super.onResume();
	}

	/**
	 * Méthode gérant le démarrage du GPS
	 */
	private void startListening() {
		this.myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	/**
	 * Méthode gérant l'arrêt du GPS
	 */
	private void stopListening() {
		if (this.myManager != null)
			this.myManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String arg0) { 
		Log.e("GPS", "provider disabled " + arg0); 
	} 
	@Override
	public void onProviderEnabled(String arg0) { 
		Log.e("GPS", "provider enabled " + arg0); 
	} 
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) { 
		Log.e("GPS", "status changed to " + arg0 + "-" + arg1 + "-" ); 
	}

	/**
	 * Calcule la position sur le plan à partir de la position GPS
	 * @param location Position GPS
	 * @return Point contenant la position en x et en y sur le plan
	 */
	private PointF gpsToPointPlan(Location location){
		//Formule pour le calcul de la position
		
			float x = (float) (1-(hautGauche.y - location.getLatitude())/(hautGauche.y-basGauche.y))*carteWidth; 
			float y = (float) ((location.getLongitude() - hautGauche.x)/(hautDroite.x-hautGauche.x)*carteHeight);
		
		return new PointF(x, y);
	}

	/**
	 * Change le plan affiché
	 * @param carte		Carte à afficher
	 * @param location	Position GPS
	 */
	private void choixPlan(int carte, Location location)
	{
		//Efface toutes les vues déjà visibles sur le layout rlPlan
		this.rlPlan.removeAllViews();
		//Ajoute la vue plan au layout rlPlan
		this.rlPlan.addView(ivPlan);

		if (carte == 1)
		{
			btnCarte1.setEnabled(false);
			btnCarte2.setEnabled(true);

			this.ivPlan.setImageDrawable(getResources().getDrawable(R.drawable.carte_interieure));

			this.carteHeight = getResources().getDrawable(R.drawable.carte_interieure).getIntrinsicHeight();
			this.carteWidth = getResources().getDrawable(R.drawable.carte_interieure).getIntrinsicWidth();

			hautGauche = new PointF((float)-3.56224, (float)48.27303);
			hautDroite = new PointF((float)-3.55301, (float)48.27306);
			basGauche = new PointF((float)-3.56220, (float)48.26758);
		}
		else if (carte == 2)
		{
			btnCarte1.setEnabled(true);
			btnCarte2.setEnabled(false);

			this.ivPlan.setImageDrawable(getResources().getDrawable(R.drawable.carte_exterieure));

			this.carteHeight = getResources().getDrawable(R.drawable.carte_exterieure).getIntrinsicHeight();
			this.carteWidth = getResources().getDrawable(R.drawable.carte_exterieure).getIntrinsicWidth();

			hautGauche = new PointF((float)-3.5924, (float)48.29378); 
			hautDroite = new PointF((float)-3.5350, (float)48.29372);  
			basGauche = new PointF((float)-3.5925, (float)48.25630); 
		}
		//Affichage de la position GPS
		affichagePosition(location);
		//Affichage des tentes visibles sur la carte choisie
		affichageTentes();
	}

	/**
	 * Méthode gérant l'affichage de la position GPS
	 * @param location	Position du GPS
	 */
	private void affichagePosition(Location location)
	{
		//Efface le point représentant la position
		rlPlan.removeView(ivPosition);

		gps = location;

		//Si la position n'est pas déterminée, affichage d'un message d'erreur
		if (location == null) {
			this.tvPrecision.setText(R.string.positionIndeterminee);
			this.tvPrecision.setBackgroundColor(Color.WHITE);
			this.tvPrecision.setTextColor(Color.RED);
		}
		else
		{
			//Si la position GPS est dans la carte, affichage de la précision et de la position
			if (dansCarte(gps))
			{			
				this.tvPrecision.setText( getString(R.string.precision) + location.getAccuracy() + getString(R.string.metres));
				this.tvPrecision.setBackgroundColor(Color.TRANSPARENT);
				this.tvPrecision.setTextColor(Color.WHITE);

				pointCarte = gpsToPointPlan( gps );
				ivPosition.setPadding( (int) pointCarte.x - 9, (int) pointCarte.y - 9, 0, 0);
				rlPlan.addView(ivPosition);
			}
			//Position GPS en dehors du plan, affichage d'un message d'erreur
			else
			{
				this.tvPrecision.setText(R.string.horsSite);
				this.tvPrecision.setBackgroundColor(Color.WHITE);
				this.tvPrecision.setTextColor(Color.RED);
			}
		}
	}

	/**
	 * Méthode gérant l'affichage des tentes sur le plan
	 */
	private void affichageTentes()
	{
		//Récupération de la liste des tentes contenue dans la base de donnée SQLite
		tentes = maDB.getTentes();

		//Affichage des différentes tentes
		for (final Tente tente : tentes) {

			if ( dansCarte(tente.location) )
			{
				Log.d("Tente", "tente");
				ImageView ivTente = new ImageView(this);
				ivTente.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));

				//Récupération de la position sur le plan et affichage sur le plan
				this.pointCarte = gpsToPointPlan(tente.location);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins((int) this.pointCarte.x - 9, (int)pointCarte.y - 9, 0, 0);               
				rlPlan.addView(ivTente, layoutParams);

				//Si l'on vient de l'activité tente, affichage de l'infobulle de la tente choisie
				if (idTenteCarte == tente.idTente) affichageInfoBulle(tente);

				//Gestion du clic sur une tente
				ivTente.setOnClickListener( new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						//Si une infobulle est déjà affichée, on l'enlève
						if (infobulle == true)
						{
							rlPlan.removeView(llInfoBulle);
							infobulle = false;
						}
						//Sinon, on l'affiche
						else {
							affichageInfoBulle(tente);
						}
					}	
				});
			}
		}
	}

	/**
	 * Détermine si une position est sur la carte affichée
	 * @param location	Position GPS
	 * @return	true (vrai) si la position se trouve sur la carte, false (faux) sinon
	 */
	private boolean dansCarte(Location location)
	{
	       Log.d("GPS", "Longitude : " + location.getLongitude());
	        Log.d("GPS", "Latitude : " + location.getLatitude());
		if(location.getLongitude() > hautGauche.x && location.getLongitude() < hautDroite.x && location.getLatitude() < hautGauche.y && location.getLatitude() > basGauche.y) 
			return true;
		else
			return false;
	}

	/**
	 * Calcule la distance séparant la position gps d'une tente
	 * @return	Chaine de caractère
	 */
	private String distanceTente()
	{
		//Si la position n'est pas déterminée, affichage d'un message d'erreur
		if (gps == null)
		{
			return "Distance indeterminée";
		}
		else
		{
			if (gps.getLongitude() !=0 && gps.getLatitude() !=0)
			{	
				//Calcul de la distance
				double distance = Math.floor( gps.distanceTo(locationTente) +0.5);

				return "à " + distance + " mètres";
			}
			else return "Distance indeterminée";
		}

	}

	/**
	 * Affichage d'une infobulle au dessus d'une tente
	 * @param tente	Tente pour laquelle on souhaite l'affichage de l'infobulle
	 */
	private void affichageInfoBulle(Tente tente)
	{
		//Récupération de la position de l'infobulle
		this.pointCarte = gpsToPointPlan(tente.location);
		RelativeLayout.LayoutParams layoutParamsInfoBulle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParamsInfoBulle.setMargins((int) this.pointCarte.x , (int)pointCarte.y - 55, 0, 0);               

		//Affichage du nom de la tente
		tvNomTente.setText(tente.nom);
		rlPlan.addView(llInfoBulle, layoutParamsInfoBulle);

		locationTente.setLatitude( tente.location.getLatitude() );
		locationTente.setLongitude( tente.location.getLongitude() );

		//Affichage de la distance de la tente par rapport à la position GPS
		tvDistanceTente.setText(distanceTente());
		
		infobulle = true;
	}
}