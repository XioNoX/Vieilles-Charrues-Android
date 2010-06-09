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

package fr.asso.vieillescharrues.parseurs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import fr.asso.vieillescharrues.db.DB;

/**
 * Classe gérant le Thread utilisé pour mettre à jour les flux
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Updater implements Runnable{

	// Liste des URL utilisées par l'application
	private static final String FACEBOOK_URL = "http://www.facebook.com/feeds/page.php?format=atom10&id=289571039510";
	private static final String TWITTER_URL = "http://twitter.com/statuses/user_timeline/37687733.rss";
	private static final String SITEOFF_URL = "http://www.vieillescharrues.asso.fr/news/news/actus?format=feed&type=rss";
	private static final String PLANNING_URL = "http://www.vieillescharrues.asso.fr/programmation.xml";
	private static final String ARTISTES_URL = "http://www.vieillescharrues.asso.fr/artistes.xml";
	private static final String PATH = "/data/data/fr.asso.vieillescharrues/";

	private DB dbSQLite;
	private final static Object LOCK = new Object();

	private final Handler handler;
	private final Context context;
	private final int typeMaj;

	/**
	 * Constructeur
	 * @param handler   Handler qui vas traiter les message envoyé par la classe
	 * @param context   Context qui execute le thread
	 * @param typeMaj   Type de la MAJ demandée (1 : News, 2 : programmation) 
	 */
	public Updater(Handler handler, Context context, int typeMaj) {

		this.handler = handler;
		this.context = context;
		this.typeMaj = typeMaj;
	}

	/**
	 * Méthode utilisée pour envoyer un message au handler
	 * @param type Type de message à envoyer (1 : Début, 2 : Terminé, 3 : Erreur)
	 */
	private void envoiMessage(int type) {
		final Message message = Message.obtain();
		message.what = type;
		handler.sendMessage(message);
	}

	@Override
	public void run() {
		dbSQLite = new DB(this.context);
		synchronized (LOCK) { //Empèche l'éxécution d'une 2nd instance du thread.
			try {
				if(typeMaj == 1) //MAJ des News
				{
					envoiMessage(1);
					TelechargeFichier.DownloadFromUrl(new URL(FACEBOOK_URL), "facebook.xml"); //Télécharge les fichiers
					TelechargeFichier.DownloadFromUrl(new URL(TWITTER_URL), "twitter.xml");
					TelechargeFichier.DownloadFromUrl(new URL(SITEOFF_URL), "siteoff.xml");
					dbSQLite.deleteTableArticles();
					RSSHandler rh = new RSSHandler();
					rh.updateArticles(this.context, new File(PATH,"facebook.xml")); //Met a jour la base de donnée
					rh.updateArticles(this.context, new File(PATH,"twitter.xml"));
					rh.updateArticles(this.context, new File(PATH,"siteoff.xml"));
					envoiMessage(2);
				}
				else if(typeMaj == 2){  //MAJ de la programmation
					envoiMessage(1);
					TelechargeFichier.DownloadFromUrl(new URL(PLANNING_URL), "planning.xml");
					TelechargeFichier.DownloadFromUrl(new URL(ARTISTES_URL), "artistes.xml");
					dbSQLite.deleteTableConcerts();
					dbSQLite.deleteTableArtistes();
					RSSProgHandler rh = new RSSProgHandler();
					rh.updateArticles(this.context, new File(PATH,"planning.xml"));
					rh.updateArticles(this.context, new File(PATH,"artistes.xml"));
					envoiMessage(2);
				}
			}catch (IOException e) { // Si impossible de télécharger un des flux (problème réseau)
				envoiMessage(3);
			}
		}
	}
}
