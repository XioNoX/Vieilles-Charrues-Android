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

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.carte.Plan;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.db.Tente;
import fr.asso.vieillescharrues.programme.CaseListe;
import fr.asso.vieillescharrues.programme.ListeAdapter;

/**
 * Classe gérant l'activité Tentes
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Tentes extends ListActivity{

	private DB tentesDB;
	private List<Tente> tentes;

	/**
	 * Création de l'activité Tentes
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.liste_tente);

		getListView().setOnCreateContextMenuListener(this);

		tentesDB = new DB(this);
	}

	/**
	 * Recharge la liste à la création ou au retour à l'activité
	 */
	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	/**
	 * Remplit la liste des tentes
	 */
	private void fillData() {

		ListeAdapter  lcAda = new ListeAdapter(this);

		//Récupération des tentes depuis la base de donnée
		tentes = tentesDB.getTentes();
		for (Tente tente : tentes) {
			lcAda.addItem(new CaseListe(tente.idTente,-1 ,tente.nom, getString(R.string.tenteLatitude) + tente.location.getLatitude() + "\n" + getString(R.string.tenteLongitude) + tente.location.getLongitude(),false));
		}
		setListAdapter(lcAda);

		TextView tvInfos = (TextView) findViewById(R.id.tvInfos);

		//Si la liste des tentes est vide, affichage d'un message explicatif
		if (tentes.size()==0)
		{
			tvInfos.setText(R.string.tenteMessageAjout);
		}
		//Sinon, affichage d'un message
		else
		{
			tvInfos.setText(R.string.tenteMessageRetrouve);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1000, 0, getString(R.string.tenteAjout)).setIcon(android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1000){
			Intent i = new Intent(Tentes.this, AjoutTente.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.action);
		menu.add(R.string.supprimer);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, Plan.class);
		i.putExtra("idTente", v.getId());
		startActivityForResult(i, 0);
	}


	@Override // Selection d'un item du menu contextuel
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		int idTente = tentes.get((int) info.id).idTente;
		tentesDB.supprimerTente(idTente);
		fillData();
		return true;
	}
}
