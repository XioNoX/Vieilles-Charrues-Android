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

import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import fr.asso.vieillescharrues.R;
import fr.asso.vieillescharrues.db.Concert;
import fr.asso.vieillescharrues.db.DB;

/**
 * Classe gerant les alarmes
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class NotificationReceiver extends BroadcastReceiver {

	private Context ctx;
	DB db;
	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		db = new DB(ctx);
		String action = intent.getAction();
		if (action.equals("fr.asso.vieillescharrues.action.tous_favoris")) activerAlarmes();
		else if (action.equals("fr.asso.vieillescharrues.action.modif_favoris")) majFavoris(intent);
		else if (action.equals("fr.asso.vieillescharrues.action.alarme_sonne"))
			createNotification(intent.getIntExtra("concertId", -1));
	}

	/**
     * Créé la notification lorsque l'alarme se déclanche
     * @param concertId  Id du concert qui devra etre notifié
     */
	private void createNotification(int concertId) {
		SharedPreferences prefs = ctx.getSharedPreferences("fr.asso.vieillescharrues", Context.MODE_PRIVATE);
		if (concertId == -1) return;
		if (!prefs.getBoolean("cbAlarme", true))  return;
		Concert concert = db.getFicheConcert(concertId);


		long startTime = concert.heureDebut.getTime();
		int delayMins = prefs.getInt("etDuree", 15);
		if (System.currentTimeMillis() < (startTime - (delayMins * 60 * 1000)))return;

		Notification notification = new Notification(R.drawable.indien, db.getNomArtiste(concert.idArtiste), startTime);
		Boolean vibrate = prefs.getBoolean("cbVibrer", true);
		if (vibrate) notification.defaults |= Notification.DEFAULT_VIBRATE;

		CharSequence title = db.getNomArtiste(concert.idArtiste);
		CharSequence text = concert.sceneNumToNom();

		Intent notificationIntent = new Intent(ctx, UnArtiste.class);
		notificationIntent.putExtra("idArtiste", concert.idArtiste);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
		notification.setLatestEventInfo(ctx, title, text, contentIntent);

		NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(concertId, notification);
	}

	 /**
     * Methode appelée lorsque un concert est mis ou enlevé des favoris
     * @param intent Intent créé lors d'une mise à jour
     */
	private void majFavoris(Intent intent) {

		String actionType = intent.getStringExtra("action");
		int concertId = intent.getIntExtra("concertId", -1);

		if (actionType.equals("ajouter")) {
			activerAlarme(db.getFicheConcert(concertId));
		} else if (actionType.equals("supprimer")) {
			supprimerAlarme(concertId);
		} else if (actionType.equals("ackAlarme")) {
			ackAlarme(concertId);
		} else if (actionType.equals("modifPreferences")) {
			activerAlarmes();
		}
	}

    /**
     * Supprime une alarme
     * @param concertId  Id du concert dont l'alarme est à enlevé
     */
	private void supprimerAlarme(int concertId) {
		Concert concert = db.getFicheConcert(concertId);
		if (concert == null) {
			return;
		}
		PendingIntent alarmIntent = intentAlarmeEnCours(concert);
		AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(alarmIntent);
	}

    /**
     * Methode apellée lorsque l'on clique sur une notification (enlève la notification)
     * @param concertId  Id du concert dont la notification est à enlever
     */
	private void ackAlarme(int concertId) {
		NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(concertId);
	}

    /**
     * Methode activant des alarmes pour tous les favoris
     * utilisée au démarrage et lors de la modification des preferences.
     */
	private void activerAlarmes() {
		List<Concert> favorisConcert = db.getFavorisConcert();
		for (Concert concert : favorisConcert) {
			activerAlarme(concert);                       
		}
	}

    /**
     * Crée une alarme
     * @param concert  Concert devant étre averti
     */
	private void activerAlarme(Concert concert) {
		SharedPreferences prefs = ctx.getSharedPreferences("fr.asso.vieillescharrues", Context.MODE_PRIVATE);
		int etDuree = prefs.getInt("etDuree", 15);

		long alarmTime = concert.heureDebut.getTime() - (etDuree * 60 * 1000);
		if (alarmTime < System.currentTimeMillis()) return;
		AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTime, intentAlarmeEnCours(concert));
	}
	
    /**
     * Crée un intent qui lancera la notification
     * @param concert  Concert devant étre notifié
     * @return PendingIntent Intent
     */
	private PendingIntent intentAlarmeEnCours(Concert concert) {
		Intent notifyIntent = new Intent("fr.asso.vieillescharrues.action.alarme_sonne");
		notifyIntent.setData(Uri.parse("concert:"+ concert.concertId)); // make Intent unique
		notifyIntent.putExtra("concertId", concert.concertId);
		return PendingIntent.getBroadcast(ctx, 0, notifyIntent, PendingIntent.FLAG_ONE_SHOT);  
	}

}
