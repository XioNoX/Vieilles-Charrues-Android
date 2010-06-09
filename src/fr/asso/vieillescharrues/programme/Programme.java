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

package fr.asso.vieillescharrues.programme;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Artiste;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;
import fr.asso.vieillescharrues.parseurs.Updater;

/**
 * Classe gérant l'activité Programmation
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Programme extends ListActivity{

	private DB programmeDB;
	private List<Concert> concerts;
	private List<Artiste> artistes;
	private int lvType=1; //1 : type Jour, 2 : type Artiste

	ListView lv;
	LinearLayout llBoutonsProgramme;
	static boolean sChargement = false;
	ProgressBar ivChargement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste_programme);
		programmeDB = new DB(this);

		lv = (ListView) findViewById(android.R.id.list);

		llBoutonsProgramme = (LinearLayout) findViewById(R.id.llBoutonsProgramme);
		ivChargement = new ProgressBar(this);
		if (sChargement == true) llBoutonsProgramme.addView(ivChargement);

		fillDataJour(1);

		Button btnJeudi = (Button) findViewById(R.id.btnJeudi);
		Button btnVendredi = (Button) findViewById(R.id.btnVendredi);
		Button btnSamedi = (Button) findViewById(R.id.btnSamedi);
		Button btnDimanche = (Button) findViewById(R.id.btnDimanche);

		Button btnArtistes = (Button) findViewById(R.id.btnArtistes);

		btnJeudi.setOnClickListener(new View.OnClickListener() {  public void onClick(View arg0) { 
			lvType=1;
			fillDataJour(1);
			lv.setFastScrollEnabled(false);
		} });
		btnVendredi.setOnClickListener(new View.OnClickListener() {  public void onClick(View arg0) { 
			lvType=1;
			fillDataJour(2);
			lv.setFastScrollEnabled(false);
		} });
		btnSamedi.setOnClickListener(new View.OnClickListener() {  public void onClick(View arg0) { 
			lvType=1;
			fillDataJour(3);
			lv.setFastScrollEnabled(false);
		} });
		btnDimanche.setOnClickListener(new View.OnClickListener() {  public void onClick(View arg0) { 
			lvType=1;
			fillDataJour(4);
			lv.setFastScrollEnabled(false);
		} });

		btnArtistes.setOnClickListener(new View.OnClickListener() {  public void onClick(View arg0) { 
			lvType=2;
			fillDataArtistes();
			lv.setFastScrollEnabled(true);
		} });

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (lvType == 1)
		{
			Log.d("onListItemClick", "idcase :" + v.getId());
			Intent i = new Intent(this, UnArtiste.class);
			i.putExtra("idArtiste", v.getId());
			startActivityForResult(i, 0);
		}
		else if (lvType == 2)
		{
			Log.d("Type", "Artiste");
			Intent i = new Intent(this, UnArtiste.class);
			i.putExtra("idArtiste", artistes.get(position).idArtiste);
			startActivityForResult(i, 0);
		}
	}

	/**
     * Rempli la liste avec les artistes
     */
	protected void fillDataArtistes() {
		ListeAdapter  lcAda = new ListeAdapter(this);
		artistes = programmeDB.getArtiste();
		for (Artiste artiste : artistes) {
			lcAda.addItem(new CaseListe(artiste.idArtiste,-1 ,artiste.nom, artiste.genre + " - " + artiste.origine,false));
		}

		setListAdapter(lcAda);
	}

    /**
     * Rempli la liste avec les concerts du jour passé en parametre
     * @param jour Id du jour à afficher
     */
	protected void fillDataJour(int jour) {
		ListeAdapter  lcAda = new ListeAdapter(this);
		concerts = programmeDB.getJour(jour);
		ArrayList<Integer> listeScenes = new ArrayList<Integer>();
		for (Concert concert : concerts) {
			if(!listeScenes.contains(concert.idScene)){
				listeScenes.add(concert.idScene);
				lcAda.addItem(new CaseListe(concert.sceneNumToNom()));
			}
			lcAda.addItem(new CaseListe(concert.idArtiste,concert.concertId ,programmeDB.getNomArtiste(concert.idArtiste), Outils.horairesDebutFin(concert.heureDebut, concert.heureFin, Outils.FORMAT_HSEULE),true));
		}
		setListAdapter(lcAda);
	}

    /**
     * Lance le thread mettant à jour la base de donnée
     */
	protected void majClicked() {
		final Thread t = new Thread(new Updater(handler, getApplicationContext(),2));
		t.start();
	}

	/**
     * Handler gerant les messages en provenance du thread
     */
	public Handler handler = new Handler() {
		@Override
        public void handleMessage(Message message) {
			if (message == null)
				return;
			switch (message.what) {
			case 1:
				Toast.makeText(getApplicationContext(), getString(R.string.miseAJour), Toast.LENGTH_SHORT).show();
				sChargement = true;
				llBoutonsProgramme.addView(ivChargement);
				break;
			case 2:
				Toast.makeText(getApplicationContext(), getString(R.string.miseAJourTermine), Toast.LENGTH_SHORT).show();
				llBoutonsProgramme.removeView(ivChargement);
				sChargement = false;
				fillDataJour(1);
				break;
			case 3:
				Toast.makeText(getApplicationContext(), getString(R.string.miseAJourErreur), Toast.LENGTH_SHORT).show();
				llBoutonsProgramme.removeView(ivChargement);
				sChargement = false;
				break;

			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1000, 0, getString(R.string.actualiser)).setIcon(android.R.drawable.ic_menu_rotate);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 1000) 
		{
			majClicked();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
     * Classe contenant les elements des cellules de la liste
     */
	public static class ViewHolder {
		public TextView tvSeparateur;
		public TextView tvTitre;
		public TextView tvDescription;
		public FavoriteButton ivFavoris;
		public ImageView fleche;
	}
}
