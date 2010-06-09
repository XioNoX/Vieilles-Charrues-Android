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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Artiste;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;
import fr.asso.vieillescharrues.parseurs.TelechargeFichier;

public class UnArtiste extends Activity{

    private DB programmeDB;
    private static final String LOGTAG = "UnArtiste";
    private Artiste artiste;
    private List<Concert> concerts;
    private String msgPartager = "";
    Bitmap imageB;
    private boolean existeEnAsset = false;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        programmeDB = new DB(this);
        setContentView(R.layout.un_artiste);
        int idArtiste;

        if (icicle != null) {
            idArtiste = icicle.getInt("idArtiste");
        } else {
            Bundle extras = getIntent().getExtras();
            idArtiste = extras.getInt("idArtiste");
        }

        artiste = programmeDB.getFicheArtiste(idArtiste);

        msgPartager += "Retouve " + artiste.nom + " (" + artiste.genre + ", " + artiste.origine + ").\n"; 

        if (artiste.image != null)
        {            
            
            try {
                imageB = BitmapFactory.decodeStream(getResources().getAssets().open("artistes/imageArtiste" + artiste.idArtiste + ".jpg"));
                existeEnAsset  = true;
            } catch (IOException e) {
                Log.e(LOGTAG,"n'existe pas en asset");
            }

            
            if(!(new File(Outils.PATH+"images/imageArtiste" + artiste.idArtiste + ".jpg").exists()) && existeEnAsset == false)
            {
                try {
                    TelechargeFichier.DownloadFromUrl(new URL(artiste.image), "imageArtiste" + artiste.idArtiste + ".jpg");
                }catch (IOException e) {
                    Log.e(LOGTAG,"Impossible de télecharger l'image");
                }
            }
            try {
                if(existeEnAsset == false)
                    imageB = BitmapFactory.decodeFile(Outils.PATH+"images/imageArtiste" + artiste.idArtiste + ".jpg"); 
                Matrix matrix = new Matrix();
                float scale = ((float) 77) / imageB.getHeight();
                matrix.postScale(scale, scale); 
                BitmapDrawable newImage = new BitmapDrawable(Bitmap.createBitmap(imageB, 0, 0, imageB.getWidth(), imageB.getHeight(), matrix, true)); 
                ImageView imgView = new ImageView(this.getBaseContext());
                imgView = (ImageView)findViewById(R.id.ivArtiste);
                imgView.setImageDrawable(newImage);

            } catch (Exception e) {
                Log.e(LOGTAG,"Impossible d'afficher l'image");
            }

        }

        TextView tvNomArtiste = (TextView) this.findViewById(R.id.tvNomArtiste);
        TextView tvLienArtiste = (TextView) this.findViewById(R.id.tvLienArtiste);
        WebView wvDescription = (WebView) findViewById(R.id.wvDescription);
        TextView tvGenre = (TextView) this.findViewById(R.id.tvGenreArtiste);
        TextView tvOrigine = (TextView) this.findViewById(R.id.tvOrigineArtiste);

        tvNomArtiste.setText(artiste.nom);
        tvGenre.setText(artiste.genre);
        tvOrigine.setText(artiste.origine);        
        tvLienArtiste.setText(Html.fromHtml("<a href=\""+artiste.lien+"\">Site officiel</a>"));
        tvLienArtiste.setMovementMethod(LinkMovementMethod.getInstance());

        LinearLayout llConcerts = (LinearLayout)findViewById(R.id.llConcerts);
        concerts = programmeDB.getConcertArtiste(idArtiste);
        int nbConcerts = concerts.size();
        for (int i=0; i < nbConcerts; i++)
        {
            //TODO : Mettre dans strings.xml
            msgPartager += "Le " + concerts.get(i).JourToJour() + " " 
            + Outils.horairesDebutFin(concerts.get(i).heureDebut, concerts.get(i).heureFin,Outils.FORMAT_HSEULE)
            + " sur la scène " + concerts.get(i).sceneNumToNom() + ".\n";

            LinearLayout llConcert = new LinearLayout(this);
            LinearLayout llConcertText = new LinearLayout(this);
            llConcert.setOrientation(LinearLayout.HORIZONTAL);
            llConcertText.setOrientation(LinearLayout.VERTICAL);
            llConcerts.addView(llConcert);
            FavoriteButton favoris = new FavoriteButton(this);
            TextView tvHeure = new TextView(this);
            TextView tvScene = new TextView(this);

            tvHeure.setPadding(5, 0, 0, 0);
            tvScene.setPadding(5, 0, 0, 0);

            tvScene.setTextColor(Color.BLACK);

            tvHeure.setText(concerts.get(i).JourToJour() + " - " + Outils.horairesDebutFin(concerts.get(i).heureDebut, concerts.get(i).heureFin,Outils.FORMAT_HSEULE));
            tvScene.setText(concerts.get(i).sceneNumToNom());
            favoris.setEvent(concerts.get(i).concertId);
            llConcert.addView(favoris);
            llConcertText.addView(tvHeure);
            llConcertText.addView(tvScene);
            llConcert.addView(llConcertText);
        }
        wvDescription.setBackgroundColor(Color.TRANSPARENT);
        wvDescription.loadDataWithBaseURL("http://www.vieillescharrues.asso.fr", artiste.description.replaceAll("<img.*/>",""), "text/html", "UTF-8", "about:blank");   
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1000, 0, getString(R.string.partager)).setIcon(android.R.drawable.ic_menu_share);
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
        sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, artiste.nom + " aux festival des Vieilles Charrues");
        sendMailIntent.putExtra(Intent.EXTRA_TEXT,msgPartager);
        sendMailIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendMailIntent, getString(R.string.artistePartagerCetArtiste)));
    }
}



