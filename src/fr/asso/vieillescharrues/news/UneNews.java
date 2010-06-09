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

import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Article;
import fr.asso.vieillescharrues.outils.Outils;

public class UneNews extends Activity{

	private Article article = new Article();

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.une_news);

		if (icicle != null) {
			article.articleId = icicle.getInt("articleId");
			article.source = icicle.getInt("source");
			article.date = new Date(icicle.getLong("date"));
			article.titre = icicle.getString("titre");
			article.contenu = icicle.getString("contenu");
		} else {
			Bundle extras = getIntent().getExtras();
			article.articleId = extras.getInt("articleId");
			article.source = extras.getInt("source");
			article.date = new Date(extras.getLong("date"));
			article.titre = extras.getString("titre");
			article.contenu = extras.getString("contenu");
		}

		TextView titreView = (TextView) this.findViewById(R.id.tvTitre);
		TextView dateView = (TextView) this.findViewById(R.id.tvDate);
		ImageView imageView = (ImageView) this.findViewById(R.id.ivSource);
		titreView.setTextColor(Color.BLACK);
		dateView.setTextColor(Color.BLACK);
		dateView.setText(Outils.horairesDebutFin(article.date, null, Outils.FORMAT_JOURDH));



		WebView wv = (WebView) findViewById(R.id.wvNews);
		wv.setBackgroundColor(Color.TRANSPARENT);

		if (article.source == 1) { //Facebook
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.icone_facebook48));
			titreView.setText("");
			wv.loadDataWithBaseURL("http://www.facebook.com",article.contenu,"text/html", "UTF-8", "about:blank");
		}
		else if (article.source == 2) //Twitter
		{
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.icone_twitter48));
			titreView.setText("");
			wv.loadDataWithBaseURL(null,article.contenu,"text/html", "UTF-8", "about:blank");
		}
		else if (article.source == 3) //Site Officiel
		{
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.icone_charrues48));
			titreView.setText(article.titre);
			wv.loadDataWithBaseURL(null,article.contenu,"text/html", "UTF-8", "about:blank");
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1000, 0, getText(R.string.partager)).setIcon(android.R.drawable.ic_menu_share);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 1000:    		
			sendMail();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void  sendMail() {
		Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
		sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, article.titre);
		sendMailIntent.putExtra(Intent.EXTRA_TEXT, "" + article.contenu.toString().replaceAll("\\<.*?>",""));
		sendMailIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendMailIntent, getText(R.string.menuPartagerCetteNews)));
	}
}