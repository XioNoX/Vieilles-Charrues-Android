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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;

/**
 * Classe gerant la liste des favois
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Favoris extends ListActivity {

	private DB favorisDB;
	private List<Concert> favorisConcert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liste_favoris);

		favorisDB = new DB(this);
	}

	@Override
	protected void onResume() {
		fillData();
		super.onResume();
	}

	/**
	 * Rempli la listview avec les élèments de la BDD
	 */
	private void fillData() {
		ListeAdapter  lcAda = new ListeAdapter(this);
		favorisConcert = favorisDB.getFavorisConcert();
		ArrayList<Integer> listeJours = new ArrayList<Integer>();
		for (Concert concert : favorisConcert) {
			if(!listeJours.contains(concert.idJour)){
				listeJours.add(concert.idJour);
				lcAda.addItem(new CaseListe(concert.JourToJour()));
			}
			lcAda.addItem(new CaseListe(concert.idArtiste,concert.concertId ,favorisDB.getNomArtiste(concert.idArtiste), Outils.horairesDebutFin(concert.heureDebut, concert.heureFin, Outils.FORMAT_HSEULE) + " - " + concert.sceneNumToNom(), true));      
		}
		setListAdapter(lcAda);

		TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
		if (favorisConcert.size()==0)
		{
			tvInfo.setText(R.string.favorisMessageAjoutConcert);
		}
		else
		{
			tvInfo.setText(R.string.favorisMessageAlerteConcert);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		CaseListe caseConcert = (CaseListe)getListView().getItemAtPosition(position);

		Intent i = new Intent(this, UnArtiste.class);
		i.putExtra("idArtiste", caseConcert.idArtiste);
		startActivityForResult(i, 0);
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1000, 0, getString(R.string.favorisPreferences)).setIcon(android.R.drawable.ic_menu_preferences);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1000){
			Intent i = new Intent(Favoris.this, Preferences.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}
