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

package fr.asso.vieillescharrues.tente;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.db.Tente;

/**
 * Classe gérant l'activité Ajouter Tente
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class AjoutTente extends Activity implements LocationListener {

	private LocationManager myManager;
	Location currentLocation;
	
	PointF hautGauche = new PointF((float)-3.5924, (float)48.29378); 
	PointF hautDroite = new PointF((float)-3.5350, (float)48.29372);  
	PointF basGauche = new PointF((float)-3.5925, (float)48.25630); 

	Tente tente;

	EditText etTente;
	TextView tvLatitude;
	TextView tvLongitude;
	TextView tvPrecision;

	private DB tenteDB = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajout_tente);

		this.tenteDB = new DB(this);
		this.tente = new Tente();

		this.tvLatitude = (TextView) findViewById(R.id.tvLatitude);
		this.tvLongitude = (TextView) findViewById(R.id.tvLongitude);
		this.tvPrecision = (TextView) findViewById(R.id.tvPrecision);

		final Button btnAjoutTente = (Button)findViewById(R.id.btnAjoutTente);

		this.myManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		this.currentLocation = this.myManager.getLastKnownLocation("gps");

		//Zone de saisie du nom de la tente
		this.etTente = (EditText) findViewById(R.id.etTente);

		//Bouton d'ajout de la tente
		btnAjoutTente.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View arg0) 
			{ 
				tente.nom = ""+ etTente.getEditableText();
				if ( tente.nom.equals("") ) {
					Toast.makeText(getApplicationContext(), getString(R.string.tenteOubliNom), Toast.LENGTH_SHORT).show();
				}
				else if( tente.location.getLatitude() == 0.0 && tente.location.getLongitude() == 0.0 )
				{
					Toast.makeText(getApplicationContext(), getString(R.string.positionIndeterminee), Toast.LENGTH_SHORT).show();
				}
				else if(!dansCarte(tente.location))
				{
					Toast.makeText(getApplicationContext(), getString(R.string.horsSite), Toast.LENGTH_SHORT).show();
				}

				else
				{	
					tenteDB.insertTente( tente.nom, tente.location);
					Toast.makeText(getApplicationContext(), getString(R.string.tenteAjoutee), Toast.LENGTH_SHORT).show();
					finish();
				}
			} 
		});
	}

	/**
	 * Détermine si une position est sur la carte affichée
	 * @param location  Position GPS
	 * @return  true (vrai) si la position se trouve sur la carte, false (faux) sinon
	 */
	private boolean dansCarte(Location location)
	{
		if(location.getLongitude() > hautGauche.x && location.getLongitude() < hautDroite.x && location.getLatitude() < hautGauche.y && location.getLatitude() > basGauche.y) 
			return true;
		else
			return false;
	}

	@Override
	public void onLocationChanged(Location currentLocation) {
		//Récupération de la position de la tente
		this.tente.location.setLatitude(currentLocation.getLatitude());
		this.tente.location.setLongitude(currentLocation.getLongitude());

		this.tvLatitude.setText( getString(R.string.tenteLatitude) + tente.location.getLatitude());
		this.tvLongitude.setText(getString(R.string.tenteLongitude) + tente.location.getLongitude());
		this.tvPrecision.setText(getString(R.string.precision) + currentLocation.getAccuracy() + getString(R.string.metres) );
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

	@Override
	protected void onDestroy() {
		stopListening();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		stopListening();
		super.onPause();
	}

	@Override
	protected void onResume() {
		startListening();
		super.onResume();
	}

	private void startListening() {
		this.myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	private void stopListening() {
		if (this.myManager != null)
			this.myManager.removeUpdates(this);
	}
}
