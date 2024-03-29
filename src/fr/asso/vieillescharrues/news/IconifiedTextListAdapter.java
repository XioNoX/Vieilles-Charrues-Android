package fr.asso.vieillescharrues.news;

/* $Id: BulletedTextListAdapter.java 57 2007-11-21 18:31:52Z steven $
 *
 * Copyright 2007 Steven Osborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/** @author Steven Osborn - http://steven.bitsetters.com */
public class IconifiedTextListAdapter extends BaseAdapter {

    /** Remember our context so we can use it when constructing views. */
    private Context mContext;

    private List<IconifiedText> mItems = new ArrayList<IconifiedText>();

    public IconifiedTextListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(IconifiedText it) { mItems.add(it); }

    public void setListItems(List<IconifiedText> lit) { mItems = lit; }

    /** @return The number of items in the */
    public int getCount() { return mItems.size(); }

    public Object getItem(int position) { return mItems.get(position); }

    public boolean areAllItemsSelectable() { return false; }

    public boolean isSelectable(int position) {
        try{
            return mItems.get(position).isSelectable();
        }catch (IndexOutOfBoundsException aioobe){
            return this.isSelectable(position);
        }
    }

    /** Use the array index as a unique id. */
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        IconifiedTextView btv;
        if (convertView == null) {
            btv = new IconifiedTextView(mContext, mItems.get(position));
        } else {

            btv = (IconifiedTextView) convertView;

            if ( mItems.get(position).getText().length() > 70)
                btv.setText(Html.fromHtml(mItems.get(position).getText()).toString().substring(0, 70) + "..." );
            else btv.setText((Html.fromHtml(mItems.get(position).getText()).toString()));


            btv.setIcon(mItems.get(position).getIcon());
        }
        return btv;
    }
}
