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

import java.util.Date;

/**
 * Classe représentant un concert
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Concert{
	public int concertId;
	public Date heureDebut;
	public Date heureFin;
	public int idArtiste;
	public int idScene;
	public Boolean favoris;
	public int idJour;

	/**
	 * Retourne le nom d'une scène à partir de son id
	 * @return	Une chaine de caractère contenant le nom de la scène
	 */
	public String sceneNumToNom(){
		switch (this.idScene) {
		case 4:  return "Glenmor";
		case 5:  return "Kérouac";
		case 6:  return "Xavier Grall";
		case 7:  return "Cabaret Breton";
		case 8:  return "Beach Box";
	    case 9:  return "Le Verger";


		}
		return "";
	}

	/**
	 * Retourne un jour à partir de son id
	 * @return	Une chaine de caractère contenant le jour
	 */
	public String JourToJour(){
		switch (this.idJour) {
		case 1:  return "Jeudi";
		case 2:  return "Vendredi";
		case 3:  return "Samedi";
		case 4:  return "Dimanche";
		}
		return "";
	}
}