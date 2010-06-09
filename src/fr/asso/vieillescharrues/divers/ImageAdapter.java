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

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mImageList;
        private String mPath;
        private AssetManager assetManager;
        
        public ImageAdapter(Context c, String mPath) {
            this.mContext = c;
            this.mPath = mPath;
            assetManager = mContext.getResources().getAssets();

            try {
                mImageList = assetManager.list(mPath);
            } catch (IOException e) {
                Log.e( "Partenaires", "I/O Exception List" + e.getMessage());
            }
        }

        public int getCount() {
            return mImageList.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
                        
            try {
                i.setImageBitmap(BitmapFactory.decodeStream(mContext.getResources().getAssets().open(this.mPath+"/"+mImageList[position])));
            } catch (IOException e) {
                Log.e( "Partenaires", "I/O Exception Decode" + e.getMessage());
            }
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE );
            i.setLayoutParams(new Gallery.LayoutParams(100, 100));

            return i;
        }
    }