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

package fr.asso.vieillescharrues.news;

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
import android.widget.ListView;
import android.widget.Toast;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Article;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;
import fr.asso.vieillescharrues.parseurs.Updater;
/**
 * Classe gerant la liste des news
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class News extends ListActivity {

	private DB newsDB;
	private List<Article> articles;

	//TODO Spinner qui indique l'avancement du telechargement

	@Override
	public void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			setContentView(R.layout.liste_news);
			newsDB = new DB(this);
			fillData();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, UneNews.class);
		i.putExtra("articleId", articles.get(position).articleId);
		i.putExtra("source", articles.get(position).source);
		i.putExtra("date", articles.get(position).date.getTime());
		i.putExtra("titre", articles.get(position).titre);
		i.putExtra("contenu", articles.get(position).contenu);  
		Log.d("Intent",""+ i);
		startActivityForResult(i, 0);
	}
	/**
	 * Rempli la listview avec les élèments de la BDD
	 */
	public void fillData() {
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		articles = newsDB.getArticles();
		for (Article article : articles) {
			
			if (article.source == 1) { //Facebook
				itla.addItem(new IconifiedText(article.titre, Outils.horairesDebutFin(article.date, null,Outils.FORMAT_JOURDH), getResources().getDrawable(R.drawable.icone_facebook48)));
			}
			else if (article.source == 2){ //Twitter
				itla.addItem(new IconifiedText(article.titre, Outils.horairesDebutFin(article.date, null,Outils.FORMAT_JOURDH), getResources().getDrawable(R.drawable.icone_twitter48)));
			}
			else if (article.source == 3){ //Site Officiel
				itla.addItem(new IconifiedText(article.titre, Outils.horairesDebutFin(article.date, null,Outils.FORMAT_JOURDH), getResources().getDrawable(R.drawable.icone_charrues48)));						
			}
		}
		setListAdapter(itla);
	}
	/**
	 * Lance le Thread mettant à jour la base de donnee à partir des URLs
	 */
	protected void majClicked() {
		final Thread t = new Thread(new Updater(handler, getApplicationContext(),1));
		t.start();
	}

	/**
	 * Handler gerant les messages provenant du Thread Updater
	 */
	public Handler handler = new Handler() {
		@Override
        public void handleMessage(Message message) {
			if (message == null)
				return;
			switch (message.what) {
			case 1:
				Toast.makeText(getApplicationContext(), getText(R.string.miseAJour), Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), getText(R.string.miseAJourTermine), Toast.LENGTH_LONG).show();
				fillData();	
				break;
			case 3:
				Toast.makeText(getApplicationContext(), getText(R.string.miseAJourErreur), Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1000, 0, getText(R.string.actualiser)).setIcon(android.R.drawable.ic_menu_rotate);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1000:    		
			majClicked();	
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}