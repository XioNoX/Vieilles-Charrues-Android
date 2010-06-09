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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;
import fr.asso.vieillescharrues.db.Article;
import fr.asso.vieillescharrues.db.DB;

/**
 * Classe gérant le parseur de news
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class RSSHandler extends DefaultHandler {

	private boolean inItem = false; //RSS : Item, Atom : Entry
	private boolean inDate = false; //RSS : pubDate, Atom : updated
	private boolean inTitle = false; // title
	private boolean inContenu = false; //Atom : content, RSS : description

	private Article currentArticle = new Article();

	private static final String LOGTAG = "SAX";

	private static SimpleDateFormat DATE_FACEBOOK = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'+01:00'" );
	private static SimpleDateFormat DATE_RSS = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
	// Twitter   : Fri, 02 Apr 2010 08:12:57 +0000
	// site off  : Thu, 01 Apr 2010 06:34:03 +0000 )
	// Facebook  : 2010-04-01T08:21:50+01:00
	// (Site off atom : 2010-04-01T06:34:03Z )

	private int source=0;

	private DB newsDB = null;
	Timestamp timestamp;

	/**
	 * Méthode appelée à l'ouverture du flux XML
	 */
	@Override
    public void startDocument() throws SAXException {
		currentArticle.date = new Date(0);
		currentArticle.titre = "";
		currentArticle.contenu = "";
	}

	/**
	 * Méthode appelée lorsque le parseur rencontre une balise ouvrante
	 */
	@Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
		if (name.trim().equals("title")) inTitle = true;
		else if (name.trim().equals("item") || name.trim().equals("entry")) inItem = true;
		else if (name.trim().equals("pubDate") || name.trim().equals("updated")) inDate = true;
		else if (name.trim().equals("content") || name.trim().equals("description")) inContenu = true;
	}

	/**
	 * Méthode appelée lorsque le parseur rencontre une balise fermante
	 */
	@Override
    public void endElement(String uri, String name, String qName) throws SAXException {
		if (name.trim().equals("title")) inTitle = false;
		else if (name.trim().equals("item") || name.trim().equals("entry")) inItem = false;
		else if (name.trim().equals("pubDate") || name.trim().equals("updated")) inDate = false;
		else if (name.trim().equals("content") || name.trim().equals("description")) inContenu = false;

		if (currentArticle.date != null	&& currentArticle.contenu != "" && currentArticle.titre != "") {


			newsDB.insertArticle(this.source, currentArticle.date.getTime(), currentArticle.titre.replaceFirst("Charrues: ", "").trim(), currentArticle.contenu.replaceFirst("Charrues: ", "").trim());

			currentArticle.date.setTime(0);
			currentArticle.titre = "";
			currentArticle.contenu = "";
		}
	}

	/**
	 * Méthode appelée lorsque le parseur rencontre du contenu entre balises
	 */
	@Override
    public void characters(char ch[], int start, int length) {

		String chars = new String(ch,start,length);
		if (inItem) {
			if (inDate)
				currentArticle.date.setTime(stringToLong(chars,source));
			else if (inTitle)
				currentArticle.titre += chars;
			else if (inContenu)
				currentArticle.contenu += chars;
		}
	}	

	/** 
	 * Méthode appelée pour démarrer le parseur
	 * @param ctx Contexte dans lequel est lancé le parseur
	 * @param fichier fichier à parser
	 */
	public void updateArticles(Context ctx, File fichier) {
		try {
			newsDB = new DB(ctx);
			if( fichier.getName().equals("facebook.xml") )
				this.source = 1;
			else if( fichier.getName().equals("twitter.xml") )
				this.source = 2;
			else if( fichier.getName().equals("siteoff.xml") )
				this.source = 3;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.parse(new InputSource(fichier.toURL().openStream()));
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
	}

	/** 
	 * Méthode utilisée pour convertir une date en String en Long (timetamp)
	 * @param date Date en string
	 * @param type Format de la date
	 */
	public long stringToLong(String date, int type){
		try {
			if(type == 1)
				return DATE_FACEBOOK.parse(date).getTime();
			else

				return DATE_RSS.parse(date).getTime();
		} catch (ParseException e) {
			Log.e(LOGTAG, "Erreur conversion date " + e.toString());
			return 0;
		}
	}

}
