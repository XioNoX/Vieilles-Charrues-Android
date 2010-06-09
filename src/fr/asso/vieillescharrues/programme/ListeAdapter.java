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

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.programme.Programme.ViewHolder;

public class ListeAdapter extends BaseAdapter {
	private static final int ITEM_VIEW_TYPE_CONCERT = 0;
	private static final int ITEM_VIEW_TYPE_SEPARATOR = 1;
	private static final int ITEM_VIEW_TYPE_COUNT = 2;
	private static boolean paire = false;
	private Context ctx;
	private List<CaseListe> mItems = new ArrayList<CaseListe>();

	public ListeAdapter(Context context) {
		ctx = context;
	}

	public void addItem(CaseListe lcc) 
	{ 
		mItems.add(lcc); 
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return ITEM_VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return (mItems.get(position).isSeparator()) ? ITEM_VIEW_TYPE_SEPARATOR
				: ITEM_VIEW_TYPE_CONCERT;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		LayoutInflater inflater = LayoutInflater.from(ctx);

		final int type = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();
			if (type == ITEM_VIEW_TYPE_SEPARATOR) {
				convertView = inflater.inflate(R.layout.case_separateur, parent, false);
				holder.tvSeparateur = (TextView)convertView.findViewById(R.id.tvSeparateur);
			} 
			else{
				convertView = inflater.inflate(R.layout.case_liste, parent, false);
				holder.tvTitre = (TextView)convertView.findViewById(R.id.tvTitre);
				holder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
				holder.ivFavoris = (FavoriteButton)convertView.findViewById(R.id.favoriteButton);
				holder.fleche = (ImageView)convertView.findViewById(R.id.ivFleche);  
			}
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}

		if (type == ITEM_VIEW_TYPE_SEPARATOR) {
			holder.tvSeparateur.setText(mItems.get(position).titre);
		} else {        
			if(paire){
				convertView.setBackgroundColor(Color.parseColor("#f3ebcd"));
				paire = false;
			}else{
				convertView.setBackgroundColor(Color.parseColor("#f3ebc0"));
				paire = true;
			}
			convertView.setId(mItems.get(position).idArtiste);
			if(mItems.get(position).afficherEtoile){
				holder.ivFavoris.setEvent(mItems.get(position).concertId,false);
				holder.ivFavoris.setOnClickListener(holder.ivFavoris);
			}  else holder.ivFavoris.setVisibility(View.INVISIBLE);
			holder.tvTitre.setText(mItems.get(position).titre);
			holder.tvDescription.setText(mItems.get(position).details);
		}

		return convertView;
	}

}
