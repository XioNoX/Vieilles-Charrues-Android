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

package fr.asso.vieillescharrues.divers;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;
import fr.asso.vieillescharrues.R;

/**
 * Classe gérant l'activité FAQ
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Infos extends Activity {
	ProgressBar ivChargement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infos);
		ivChargement = new ProgressBar(this);
		//LinearLayout llInfos = (LinearLayout) findViewById(R.id.llInfos);
		//llInfos.addView(ivChargement);
		WebView wv = (WebView) findViewById(R.id.wvInfos);
		//wv.setVisibility(WebView.INVISIBLE);
		wv.setBackgroundColor(Color.TRANSPARENT);
		wv.loadUrl("file:///android_asset/infos.html");	
		//llInfos.removeView(ivChargement);
		//wv.setVisibility(WebView.VISIBLE);
	}
}