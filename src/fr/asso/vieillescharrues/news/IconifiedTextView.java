package fr.asso.vieillescharrues.news;

/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $
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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout {

	private TextView mDate;
	private TextView mText;
	private ImageView mIcon;

	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);

		/* First Icon and the Text to the right (horizontal),
		 * not above and below (vertical) */
		this.setOrientation(HORIZONTAL);

		mIcon = new ImageView(context);
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		// left, top, right, bottom
		mIcon.setPadding(0, 2, 5, 0); // 5px to the right

		/* At first, add the Icon to ourself
		 * (! we are extending LinearLayout) */
		addView(mIcon,  new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		mText = new TextView(context);
		mText.setTextColor(Color.BLACK);
		
		LinearLayout ll = new LinearLayout(context);
		addView(ll, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		ll.setOrientation(VERTICAL);
		
		if (aIconifiedText.getText().length() > 70)
		{
		    mText.setText(Html.fromHtml(aIconifiedText.getText()).toString().substring(0, 70) + "...");
		}
		else
		{
			mText.setText(Html.fromHtml(aIconifiedText.getText().toString()));
		}

		/* Now the text (after the icon) */
		mDate = new TextView(context);
		mDate.setTextColor(Color.GRAY);
		mDate.setText(aIconifiedText.getDate());
		ll.addView(mDate);
		ll.addView(mText);
	}

	public void setText(String words) {
		mText.setText(words);
	}

	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}