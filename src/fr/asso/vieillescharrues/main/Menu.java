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

package fr.asso.vieillescharrues.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.carte.Plan;
import fr.asso.vieillescharrues.divers.Infos;
import fr.asso.vieillescharrues.divers.Partenaires;
import fr.asso.vieillescharrues.news.News;
import fr.asso.vieillescharrues.programme.Favoris;
import fr.asso.vieillescharrues.programme.Programme;
import fr.asso.vieillescharrues.tente.Tentes;

/**
 * Classe gérant l'activité affichant le menu principal (sous forme d'icones)
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Menu extends Activity{

	// Tableau d'entiers contenant les icones du menu
	private static final Integer[] MENU_ICONES = {
		R.drawable.menu_news, R.drawable.menu_infos,
		R.drawable.menu_prog, R.drawable.menu_favoris,
		R.drawable.menu_carte, R.drawable.menu_tente
	};

	/**
	 * Crée le menu à la création de l'activité
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		GridView gvMenu = (GridView) findViewById(R.id.gvMenu);
		gvMenu.setAdapter(new ImageAdapter(this, MENU_ICONES));
		gvMenu.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

		// XXX : A executer 1 fois puis commenter
		// A utiliser uniquement avant distribution
		// Outils.TelechargeImagesArtistes(this);

		//Lance la création de toutes les alarmes programmés.
		sendBroadcast(new Intent("fr.asso.vieillescharrues.action.tous_favoris"));

		gvMenu.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent i = null;

				switch(arg2)
				{
				case 0: //News
					i = new Intent(Menu.this, News.class);
					break;
				case 1: //Divers
					i = new Intent(Menu.this, Infos.class);
					break;
				case 2: //Programme
					i = new Intent(Menu.this, Programme.class);
					break;
				case 3: //Favoris
					i = new Intent(Menu.this, Favoris.class);
					break;
				case 4: //Plan
					i = new Intent(Menu.this, Plan.class);
//					Toast.makeText(getApplicationContext(), getString(R.string.bientotDisponible), Toast.LENGTH_LONG).show();
					break;
				case 5: //Tente
					i = new Intent(Menu.this, Tentes.class);
//					Toast.makeText(getApplicationContext(), getString(R.string.bientotDisponible), Toast.LENGTH_LONG).show();
					break;
				}
				if (i!=null) startActivity(i);

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return createAboutDialog();
		default:
			return null;
		}
	}

	/**
	 * Crée une fenetre de dialogue
	 * @return Dialog La fenetre de dialogue crée
	 */
	private Dialog createAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		TextView tvAbout = new TextView(this);
		tvAbout.setPadding(5, 0, 0, 0);

		tvAbout.setText( Html.fromHtml("<center>" +
				"Projet Open Source réalisé par :<br/>" +
				"Julien Vermet <a href=\"mailto:ju.vermet@gmail.com\">ju.vermet@gmail.com</a><br/>" +
				"Arzhel Younsi <a href=\"mailto:xionox@gmail.com\">xionox@gmail.com</a><br/>" +
				"étudiants à Polytech'Nantes<br/><br/>" +
				"Sources disponibles à l'adresse :<br/>" +
				"<a href=\"http://code.google.com/p/vieillescharrues/\">http://code.google.com/p/vieillescharrues/</a>" +
				"</center>"));
		tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
		tvAbout.setTextColor(Color.WHITE);

		builder.setTitle(getString(R.string.menuAPropos));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(tvAbout);
		builder.setPositiveButton(getString(android.R.string.ok), null);
		builder.setCancelable(true);
		return builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add(0, 1000, 0, getString(R.string.menuAPropos)).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, 1001, 0, getString(R.string.menuPartenaires)).setIcon(android.R.drawable.ic_menu_agenda);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case 1000:          
			showDialog(0);
			break;
		case 1001:          
			startActivity(new Intent(Menu.this, Partenaires.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}