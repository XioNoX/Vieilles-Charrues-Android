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
import java.util.Date;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.content.Context;
import android.util.Log;
import fr.asso.vieillescharrues.db.Artiste;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;

/**
 * Classe gérant le parseur de programmation/artistes
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class RSSProgHandler extends DefaultHandler {

	//Liste et initialisation de toutes les balises XML présentes dans les flux.
	private boolean inArtiste = false;
	private boolean inNom = false;
	private boolean inDescription = false;
	private boolean inGenre = false;
	private boolean inOrigine = false;
	private boolean inImage = false;
	private boolean inLien = false;

	private boolean inJour = false;
	private boolean inScene = false;
	private boolean inConcert = false;
	private boolean inHdebut = false;
	private boolean inHfin = false;
	private boolean inIdArtiste = false;

	private Artiste currentArtiste = new Artiste();
	private Concert currentConcert = new Concert();

	String heureDebutTmp;
	private static final String LOGTAG = "SAX-Prog";

	private int source=0;

	private DB newsDB = null;

	/**
	 * Méthode appelée à l'ouverture du flux XML
	 */
	@Override
    public void startDocument() throws SAXException {

		currentArtiste.idArtiste = -1;
		currentArtiste.description = "";
		currentArtiste.genre = "";
		currentArtiste.origine = "";
		currentArtiste.image = "";
		currentArtiste.lien = "";
		currentArtiste.nom = "";

		currentConcert.concertId = -1;
		currentConcert.heureDebut = new Date(0);
		currentConcert.heureFin = new Date(0);
		currentConcert.idArtiste = -1;
		currentConcert.idScene = -1;
		currentConcert.favoris = false;
		currentConcert.idJour = -1;
	}

	/**
	 * Méthode appelée lorsque le parseur rencontre une balise ouvrante
	 */
	@Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
		if(source == 2){ //Artiste
			if (name.trim().equals("artiste")) { inArtiste = true; currentArtiste.idArtiste = Integer.parseInt(atts.getValue("id")); } //Récupére l'atribut ID de la balise Artiste
			else if (name.trim().equals("nom")) inNom = true;
			else if (name.trim().equals("description")) inDescription = true;
			else if (name.trim().equals("genre")) inGenre = true;
			else if (name.trim().equals("origine")) inOrigine = true;
			else if (name.trim().equals("image")) inImage = true;
			else if (name.trim().equals("lien")) inLien = true;
		}
		else if (source == 1){ //Programmation
			if (name.trim().equals("jour")) { inJour = true; currentConcert.idJour = Integer.parseInt(atts.getValue("id")); }
			else if (name.trim().equals("scene")) { inScene = true; currentConcert.idScene = Integer.parseInt(atts.getValue("id")); }
			else if (name.trim().equals("concert")) { inConcert = true; currentConcert.concertId = Integer.parseInt(atts.getValue("id")); }
			else if (name.trim().equals("hDebut")) inHdebut = true;
			else if (name.trim().equals("hFin")) inHfin = true;
			else if (name.trim().equals("idArtiste")) inIdArtiste = true;
		}
	}

	/**
	 * Méthode appelée lorsque le parseur rencontre une balise fermante
	 */
	@Override
    public void endElement(String uri, String name, String qName) throws SAXException {

		if(source == 2){
			if (name.trim().equals("artiste")){ 
				inArtiste = false;
				//Insertion de l'artiste dans la BDD
				newsDB.insertArtiste(currentArtiste.idArtiste, currentArtiste.nom.trim(), currentArtiste.description,currentArtiste.origine, currentArtiste.genre, currentArtiste.image, currentArtiste.lien);
				currentArtiste.idArtiste = -1;
				currentArtiste.description = "";
				currentArtiste.genre = "";
				currentArtiste.origine = "";
				currentArtiste.image = "";
				currentArtiste.lien = "";
				currentArtiste.nom = "";

			}
			else if (name.trim().equals("nom")) inNom = false;
			else if (name.trim().equals("description")) inDescription = false;
			else if (name.trim().equals("genre")) inGenre = false;
			else if (name.trim().equals("origine")) inOrigine = false;
			else if (name.trim().equals("image")) inImage = false;
			else if (name.trim().equals("lien")) inLien = false;
		}
		else if (source == 1){
			if (name.trim().equals("jour")) inJour = false;
			else if (name.trim().equals("scene")) inScene = false;
			else if (name.trim().equals("concert")) { inConcert = false;
			//Insertion du concert dans la BDD
			newsDB.insertConcert(currentConcert.concertId, currentConcert.heureDebut.getTime(), currentConcert.heureFin.getTime(), currentConcert.idArtiste, currentConcert.idScene, currentConcert.idJour);
			currentConcert.concertId = -1;
			currentConcert.heureDebut.setTime(0);
			currentConcert.heureFin.setTime(0);
			currentConcert.idArtiste = -1;
			currentConcert.favoris = false;

			}
			else if (name.trim().equals("hDebut")) inHdebut = false;
			else if (name.trim().equals("hFin")) inHfin = false;
			else if (name.trim().equals("idArtiste")) inIdArtiste = false;

			if (currentConcert.idJour >= 0	&& currentConcert.idScene >= 0 && currentConcert.concertId >= 0 && currentConcert.idArtiste >= 0 ) {

			}
		}
	}
	
	/**
	 * Méthode appelée lorsque le parseur rencontre du contenu entre balises
	 */
	@Override
    public void characters(char ch[], int start, int length) {

		String chars = new String(ch,start,length);
		if (inJour) {
			if (inScene){
				if (inConcert){
					if (inIdArtiste) currentConcert.idArtiste = Integer.parseInt(chars);
					else if (inHdebut) currentConcert.heureDebut.setTime(Outils.stringToLong(chars,currentConcert.idJour));  
					else if (inHfin) currentConcert.heureFin.setTime(Outils.stringToLong(chars,currentConcert.idJour)); 		
				}
			}
		}
		else if (inArtiste) {
			if (inScene);
			else if (inNom) currentArtiste.nom += chars;
			else if (inDescription) currentArtiste.description += chars;
			else if (inOrigine) currentArtiste.origine += chars;
			else if (inGenre) currentArtiste.genre += chars;
			else if (inImage) currentArtiste.image += chars;
			else if (inLien) currentArtiste.lien += chars;

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
			if( fichier.getName().equals("planning.xml") )
				this.source = 1;
			else if( fichier.getName().equals("artistes.xml") )
				this.source = 2;

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.parse(new InputSource(fichier.toURL().openStream()));
		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
	}


}
