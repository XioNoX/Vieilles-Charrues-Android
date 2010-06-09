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
import android.os.Bundle;
import android.widget.Gallery;
import fr.asso.vieillescharrues.R;

/**
 * Classe gérant l'activité Partenaires
 * 
 * @author Julien VERMET / Arzhel YOUNSI
 * @version 1.0
 */
public class Partenaires extends Activity {
    
    private String mPath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partenaires);
        
        mPath = "partenaires/parrains";
        Gallery gParrainsOfficiels = (Gallery) findViewById(R.id.gParrainsOfficiels);
        gParrainsOfficiels.setAdapter(new ImageAdapter(this, mPath));

        mPath = "partenaires/partenairesofficiels";
        Gallery gPartenairesOfficiels = (Gallery) findViewById(R.id.gPartenairesOfficiels);
        gPartenairesOfficiels.setAdapter(new ImageAdapter(this, mPath));

        mPath = "partenaires/partenaires";
        Gallery gPartenaires = (Gallery) findViewById(R.id.gPartenaires);
        gPartenaires.setAdapter(new ImageAdapter(this, mPath));
        
        mPath = "partenaires/partenairesmedias";
        Gallery gPartenairesMedias = (Gallery) findViewById(R.id.gPartenairesMedias);
        gPartenairesMedias.setAdapter(new ImageAdapter(this, mPath));
    }


}