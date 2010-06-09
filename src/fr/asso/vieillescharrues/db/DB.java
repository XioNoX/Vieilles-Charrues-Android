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

package fr.asso.vieillescharrues.db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

/**
 * Classe gérant la base de donnée SQLite
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class DB {

	private static final String CREER_TABLE_ARTICLES = "CREATE TABLE IF NOT EXISTS articles " +
	"(articleId integer primary key autoincrement, source int not null," +
	" date INTEGER not null,titre text not null, contenu text);";

	private static final String CREER_TABLE_CONCERTS = "CREATE TABLE IF NOT EXISTS concerts " +
	"(concertId INTEGER primary key, heureDebut INTEGER, heureFin INTEGER," +
	" idArtiste INTEGER not null, idScene INTEGER, idJour INTEGER, favoris BOOL);";

	private static final String CREER_TABLE_ARTISTES = "CREATE TABLE IF NOT EXISTS artistes " +
	"(idArtiste INTEGER primary key, nom TEXT not null, description TEXT not null," +
	" origine TEXT, genre TEXT, image TEXT, lien TEXT);";

	private static final String CREER_TABLE_TENTES = "CREATE TABLE IF NOT EXISTS tentes " +
	"(idTente integer primary key autoincrement, nom TEXT not null, latitude DOUBLE," +
	" longitude DOUBLE);";

	private static final String TABLE_ARTICLES = "articles";
	private static final String TABLE_CONCERTS = "concerts";
	private static final String TABLE_ARTISTES = "artistes";
	private static final String TABLE_TENTES = "tentes";

	public static final String DATABASE_NAME = "database.db";
	private static final String LOGTAG = "db";
	private SQLiteDatabase db;

	private Context ctx;

	/**
	 * Constructeur de la classe DB
	 * @param ctx	Context de l'activité qui a créé l'objet DB
	 */
	public DB(Context ctx) {
		this.ctx = ctx;

		try {
			open();
			db.execSQL(CREER_TABLE_ARTICLES);
			db.execSQL(CREER_TABLE_CONCERTS);
			db.execSQL(CREER_TABLE_ARTISTES);
			db.execSQL(CREER_TABLE_TENTES);
			close();
		} catch (SQLException e) {
			Log.e(LOGTAG, e.toString());
		}

	}

	/**
	 * Ouvre la base SQLite ou la crée si elle n'existait pas
	 */
	public void open(){
		db = ctx.openOrCreateDatabase(DATABASE_NAME,0, null);
	}

	/**
	 * Ferme la base SQLite
	 */
	public void close(){
		db.close();
	}

	/**
	 * Insère un article dans la base SQLite
	 * @param source	Source de l'article
	 * @param date		Date de l'article
	 * @param titre		Titre de l'article
	 * @param contenu	Contenu de l'article
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite		
	 */
	public boolean insertArticle(int source, Long date, String titre, String contenu){
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("date",date);
		values.put("titre", titre);
		values.put("contenu", contenu);
		open();
		boolean result = db.insert(TABLE_ARTICLES, null, values) > 0;
		close();
		return result;
	}

	/**
	 * Insère un artiste dans la base SQLite
	 * @param idArtiste		ID de l'artiste
	 * @param nom			Nom de l'artiste
	 * @param description	Description de l'artiste
	 * @param origine		Origine de l'artiste
	 * @param genre			Genre de l'artiste
	 * @param image			Image de l'artiste
	 * @param lien			Lien vers le site internet de l'artiste
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite	
	 */
	public boolean insertArtiste(int idArtiste, String nom, String description, String origine, String genre, String image, String lien){
		ContentValues values = new ContentValues();
		values.put("idArtiste", idArtiste);
		values.put("nom", nom);
		values.put("description", description);
		values.put("origine", origine);
		values.put("genre", genre);
		values.put("image", image.toString());
		values.put("lien", lien.toString());
		open();
		boolean result = db.insert(TABLE_ARTISTES, null, values) > 0;
		close();
		return result;
	}

	/**
	 * Insère un concert dans la base SQLite
	 * @param concertId		Id du concert
	 * @param heureDebut	Heure de début du concert
	 * @param heureFin		Heure de fin du concert
	 * @param idArtiste		Id de l'artiste qui joue dans le concert
	 * @param idScene		Id de la scène où à lieu le concert
	 * @param idJour		Id du jour du concert
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean insertConcert(int concertId, Long heureDebut, Long heureFin, int idArtiste, int idScene, int idJour){
		ContentValues values = new ContentValues();
		values.put("concertId", concertId);
		values.put("heureDebut", heureDebut);
		values.put("heureFin", heureFin);
		values.put("idArtiste", idArtiste);
		values.put("idScene", idScene);
		values.put("idJour", idJour);
		open();
		boolean result = db.insert(TABLE_CONCERTS, null, values) > 0;
		close();
		return result;
	}

	/**
	 * Insère une tente dans la base SQLite 
	 * @param nom		Nom donné à la tente
	 * @param latitude	Latitude de la tente
	 * @param longitude	Longitude de la tente
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean insertTente(String nom, Location location){
		ContentValues values = new ContentValues();
		values.put("nom", nom);
		values.put("latitude", location.getLatitude());
		values.put("longitude", location.getLongitude());
		open();
		boolean result = db.insert(TABLE_TENTES, null, values) > 0;
		close();
		return result;
	}

	/**
	 * Efface les entrées de la table Articles
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean deleteTableArticles() { 
		open();
		boolean result = db.delete(TABLE_ARTICLES, null, null) > 0;
		close();
		return result;
	}

	/**
	 * Efface les entrées de la table Artistes
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean deleteTableArtistes() { 
		open();
		boolean result = db.delete(TABLE_ARTISTES, null, null) > 0;
		close();
		return result;
	}

	/**
	 * Efface les entrées de la table Concerts
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean deleteTableConcerts() { 
		open();
		boolean result = db.delete(TABLE_CONCERTS, null, null) > 0;
		close();
		return result;
	}

	/**
	 * Efface les entrées de la table Tentes
	 * @return vrai(true) si l'insertion est correcte ou faux(false) si une erreur s'est produite
	 */
	public boolean deleteTableTentes() { 
		open();
		boolean result = db.delete(TABLE_TENTES, null, null) > 0;
		close();
		return result;
	}

	/**
	 * Récupère les articles dans la base SQLite
	 * @return Une liste d'articles
	 */
	public List<Article> getArticles() {
		ArrayList<Article> articles = new ArrayList<Article>();
		try {
			open();
			Cursor c = db.query(TABLE_ARTICLES, new String[] {"articleID", "source", "date", "titre", "contenu"},
					null, null, null, null, "date DESC");
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Article article = new Article();
				article.articleId = c.getInt(0);
				article.source = c.getInt(1);
				article.date = new Date(c.getLong(2));
				article.titre = c.getString(3);
				article.contenu = c.getString(4);
				articles.add(article);				
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return articles;
	}

	/**
	 * Récupère les artistes dans la base SQLite
	 * @return Une liste d'artistes
	 */
	public List<Artiste> getArtiste() {
		ArrayList<Artiste> artistes = new ArrayList<Artiste>();
		try {
			open();
			Cursor c = db.query(TABLE_ARTISTES, new String[] {"idArtiste", "nom", "description", "origine", "genre", "image", "lien"},
					null, null, null, null, "UPPER(nom)");
			int numRows = c.getCount();
			c.moveToFirst();

			for (int i = 0; i < numRows; ++i) {
				Artiste artiste = new Artiste();
				artiste.idArtiste = c.getInt(0);
				artiste.nom = c.getString(1);
				artiste.description = c.getString(2);
				artiste.origine = c.getString(3);
				artiste.genre = c.getString(4);
				artiste.image = c.getString(5);
				artiste.lien = c.getString(6);
				artistes.add(artiste);              
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return artistes;
	}

	/**
	 * Récupère la fiche d'un artiste dans la base SQLite
	 * @param idArtiste	Id de l'artiste concerné
	 * @return Un artiste
	 */
	public Artiste getFicheArtiste(int idArtiste)
	{
		open();
		Cursor c = db.query(TABLE_ARTISTES, new String[] {"idArtiste", "nom", "description", "origine", "genre", "image", "lien"},
				"idArtiste="+ idArtiste, null, null, null, null);
		c.moveToFirst();
		Log.e("db", "id artiste :" + idArtiste);
		Artiste artiste = new Artiste();
		artiste.idArtiste = c.getInt(0);
		artiste.nom = c.getString(1);
		artiste.description = c.getString(2);
		artiste.origine = c.getString(3);
		artiste.genre = c.getString(4);
		artiste.image = c.getString(5);
		artiste.lien = c.getString(6);
		c.close();
		close();

		return artiste;
	}

	/**
	 * Récupère le nom d'un artiste dans la base SQLite
	 * @param idArtiste	Id de l'artiste concerné
	 * @return	Une chaine
	 */
	public String getNomArtiste(int idArtiste)
	{
		open();
		Cursor c = db.query(TABLE_ARTISTES, new String[] {"nom"},
				"idArtiste="+ idArtiste, null, null, null, null);

		if ( c.getCount() !=0 )
		{
			c.moveToFirst();
			String result = c.getString(0);
			c.close();
			close();
			return result;
		}	
		else {
			c.close();
			close();
			return "";
		}
	}

	/**
	 * Récupère les concerts pour un jour donné dans la base SQLite
	 * @param jour	Jour pour lequel on veut récupérer les concerts
	 * @return Une liste de concerts
	 */
	public List<Concert> getJour(int jour) {
		ArrayList<Concert> concerts = new ArrayList<Concert>();
		try {
			open();
			Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertID", "heureDebut", "heureFin", "idArtiste", "idScene", "idJour", "favoris"},
					"idJour="+jour, null, null, null, "idScene");
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Concert concert = new Concert();
				concert.concertId = c.getInt(0);
				concert.heureDebut = new Date(c.getLong(1));
				concert.heureFin = new Date(c.getLong(2));
				concert.idArtiste = c.getInt(3);
				concert.idScene = c.getInt(4);
				concert.idJour = c.getInt(5);
				if(c.getString(6) == "1")
					concert.favoris = true;
				else
					concert.favoris = false;
				concerts.add(concert);              
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return concerts;
	}

	/**
	 * Récupère les concerts d'une scène dans la base SQLite
	 * @param jour	Jour pour lequel on veut récupérer les concerts
	 * @param scene	Scène pour laquelle on veut récupérer les concerts
	 * @return Une liste de concerts
	 */
	public List<Concert> getJourScene(int jour, int scene) {
		ArrayList<Concert> concerts = new ArrayList<Concert>();
		try {
			open();
			Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertID", "heureDebut", "heureFin", "idArtiste", "idScene", "idJour", "favoris"},
					"idJour="+jour + " AND idScene="+scene, null, null, null, "idScene");
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Concert concert = new Concert();
				concert.concertId = c.getInt(0);
				concert.heureDebut = new Date(c.getLong(1));
				concert.heureFin = new Date(c.getLong(2));
				concert.idArtiste = c.getInt(3);
				concert.idScene = c.getInt(4);
				concert.idJour = c.getInt(5);
				if(c.getString(6) == "1")
					concert.favoris = true;
				else
					concert.favoris = false;
				concerts.add(concert);              
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return concerts;
	}

	/**
	 * Récupère la fiche d'un concert dans la base SQLite
	 * @param idConcert	Id du concert concerné
	 * @return Un concert
	 */
	public Concert getFicheConcert(int idConcert)
	{
		open();
		//        Cursor c = db.query(TABLE_ARTISTES, new String[] {"idArtiste", "nom", "description", "origine", "genre", "image", "lien"},
		//                "idArtiste="+ idConcert, null, null, null, null);
		Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertID", "heureDebut", "heureFin", "idArtiste", "idScene", "idJour", "favoris"},
				"concertId="+idConcert, null, null, null, null);
		c.moveToFirst();
		Concert concert = new Concert();
		concert.concertId = c.getInt(0);
		concert.heureDebut = new Date(c.getLong(1));
		concert.heureFin = new Date(c.getLong(2));
		concert.idArtiste = c.getInt(3);
		concert.idScene = c.getInt(4);
		concert.idJour = c.getInt(5);
		if(c.getString(6) == "1")
			concert.favoris = true;
		else
			concert.favoris = false;   
		c.close();
		close();

		return concert;
	}

	/**
	 * Récupère les concerts d'un artiste dans la base SQLite
	 * @param idArtiste	Id de l'artiste concerné
	 * @return Une liste de concerts
	 */
	public List<Concert> getConcertArtiste(int idArtiste) {
		ArrayList<Concert> concerts = new ArrayList<Concert>();
		try {
			open();
			Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertID", "heureDebut", "heureFin", "idArtiste", "idScene", "idJour", "favoris"},
					"idArtiste="+ idArtiste, null, null, null, null);
			int numRows = c.getCount();
			c.moveToFirst();

			for (int i = 0; i < numRows; ++i) {
				Concert concert = new Concert();
				concert.concertId = c.getInt(0);
				concert.heureDebut = new Date(c.getLong(1));
				concert.heureFin = new Date(c.getLong(2));
				concert.idArtiste = c.getInt(3);
				concert.idScene = c.getInt(4);
				concert.idJour = c.getInt(5);
				if(c.getString(6) == "1")
					concert.favoris = true;
				else
					concert.favoris = false;
				concerts.add(concert);              
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return concerts;
	}

	/**
	 * Récupère les concerts en favoris dans la base SQLite
	 * @return Une liste de concerts
	 */
	public List<Concert> getFavorisConcert() {
		ArrayList<Concert> favorisConcert = new ArrayList<Concert>();
		try { 
			open();
			Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertId", "heureDebut", "heureFin", "idArtiste", "idScene", "idJour"},
					"favoris = 1", null, null, null, "idJour");
			int numRows = c.getCount();
			c.moveToFirst();

			for (int i = 0; i < numRows; ++i) {
				Concert concert = new Concert();
				concert.concertId = c.getInt(0);
				concert.heureDebut = new Date(c.getLong(1));
				concert.heureFin = new Date(c.getLong(2));
				concert.idArtiste = c.getInt(3);
				concert.idScene = c.getInt(4);
				concert.idJour = c.getInt(5);
				favorisConcert.add(concert);				
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return favorisConcert;
	}

	/**
	 * Récupère les tentes dans la base SQLite
	 * @return	Une liste de tentes
	 */
	public List<Tente> getTentes() {
		ArrayList<Tente> tentes = new ArrayList<Tente>();
		try {
			open();
			Cursor c = db.query(TABLE_TENTES, new String[] {"idTente", "nom", "latitude", "longitude"},
					null, null, null, null, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				Tente tente = new Tente();
				tente.idTente = c.getInt(0);
				tente.nom = c.getString(1);
				tente.location.setLatitude(c.getDouble(2));
				tente.location.setLongitude(c.getDouble(3));
				tentes.add(tente);				
				c.moveToNext();
			}
			c.close();
			close();
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		} 
		return tentes;
	}

	/**
	 * Supprime une tente de la base SQLite
	 * @param idTente	Id de la tente concernée
	 */
	public void supprimerTente(int idTente)
	{
		open();
		db.delete(TABLE_TENTES, "idTente="+idTente, null);
		close();
	}

	/**
	 * Ajoute un concert en favoris dans la base SQLite
	 * @param concertId Id du concert concerné
	 */
	public void ajouterFavoris(int concertId) {
		open();
		Cursor c = db.rawQuery("UPDATE " + TABLE_CONCERTS + " SET favoris=1 WHERE concertId="
				+ concertId, null);
		c.moveToFirst();   
		c.close();
		close();
		Intent intent = new Intent("fr.asso.vieillescharrues.action.modif_favoris");
		intent.putExtra("action","ajouter");
		intent.putExtra("concertId", concertId);
		ctx.sendBroadcast(intent);
	}

	/**
	 * Permet de savoir si un concert est en favoris dans la base SQLite
	 * @param concertId	Id du concert concerné
	 * @return vrai(true) si le concert est en favoris ou faux(false) s'il ne l'est pas
	 */
	public boolean enFavoris(int concertId) {
		open();
		Cursor c = db.query(TABLE_CONCERTS, new String[] {"concertId"},
				"favoris = 1 AND concertId = " + concertId , null, null, null, null);
		boolean result = c.getCount() > 0;
		c.close();
		close();
		return result;
	}

	/**
	 * Supprime un concert des favoris dans la base SQLite
	 * @param concertId Id du concert concerné
	 */
	public void supprimerFavoris(int concertId) {
		open();
		Cursor c = db.rawQuery("UPDATE " + TABLE_CONCERTS + " SET favoris=0 WHERE concertId="
				+ concertId, null);
		c.moveToFirst(); 
		c.close();
		close();

		Intent intent = new Intent("fr.asso.vieillescharrues.action.modif_favoris");
		intent.putExtra("action","supprimer");
		intent.putExtra("concertId", concertId);
		ctx.sendBroadcast(intent);
	}
}






