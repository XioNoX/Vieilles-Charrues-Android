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

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;
// TODO : A commenter
public class FavoriteButton extends ImageView implements OnClickListener {

	// utiliser que le int et pas l'objet
	protected Concert concert;
	public int concertId = 0;

	protected boolean isFavorite = false;
	protected boolean initialized = false;
	private boolean protectInitialize = true;

	public FavoriteButton(Context context){
		super(context);
	}

	public void setEvent(Concert concert){
		this.concert = concert;
		initialize();
	}
	public void setEvent(int concertId){
		this.concertId = concertId;
		initialize();
	}
	public void setEvent(int concertId,boolean protectInitialize){
		this.concertId = concertId;
		this.protectInitialize = protectInitialize;
		initialize();
	}

	public FavoriteButton(Context context, Concert concert) {
		super(context);
		this.concert = concert;

		initialize();
	}

	public FavoriteButton(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
		this.setImageResource(R.drawable.btn_star_big_off);
	}

	protected void initialize() {
		if(initialized && protectInitialize)return;
		initialized=true;
		DB programmeDB = new DB(getContext());
		if(concertId != 0)
			isFavorite = programmeDB.enFavoris(concertId);
		else isFavorite = programmeDB.enFavoris(concert.concertId);

		setImageResource();

		this.setOnClickListener(this);
	}

	protected void setImageResource() {
		if (isFavorite) {
			this.setImageResource(R.drawable.btn_star_big_on);
		} else
			this.setImageResource(R.drawable.btn_star_big_off);
	}

	public void onClick(View v) {
		DB programmeDB = new DB(getContext());
		if (isFavorite) {
			if(concertId != 0)
				programmeDB.supprimerFavoris(concertId);
			else programmeDB.supprimerFavoris(concert.concertId);
		} else {
			if(concertId != 0)
				programmeDB.ajouterFavoris(concertId);
			else programmeDB.ajouterFavoris(concert.concertId);
		}
		isFavorite = !isFavorite;
		setImageResource();
	}
}
