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

package fr.asso.vieillescharrues.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.DB;
import fr.asso.vieillescharrues.outils.Outils;

/**
 * Classe gérant le SplashScreen
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Main extends Activity {
    private static final int sStopFlash = 0;
    private static final long sSplashTime = 1000;

    /**
     * Envoie un message au handler une fois le temps écoulé
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        if(!(new File(Outils.PATH+"databases/"+DB.DATABASE_NAME).exists()))
        {
            try {
                new File(Outils.PATH+"databases/").mkdirs();
                InputStream inputStream = getResources().getAssets().open(DB.DATABASE_NAME);
                OutputStream outputStream = new FileOutputStream(Outils.PATH+"databases/"+DB.DATABASE_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer))>0){
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }




        }

        Message msg = new Message();
        msg.what = sStopFlash;
        splashHandler.sendMessageDelayed(msg, sSplashTime);
    }

    /**
     * Redirige vers le menu une fois le message reçu
     */
    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case sStopFlash:
                //Le temps est écoulé, on démarre un intent vers le menu
                Intent intent = new Intent(Main.this, Menu.class);
                startActivity(intent);
                //On termine le splashscreen pour ne pas le voir réapparaitre avec un retour
                finish();
                break;
            }
            super.handleMessage(msg);
        }
    };
}
