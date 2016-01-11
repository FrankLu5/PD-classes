/*
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;

import java.util.HashMap;

public class RenderedText extends Image {

	private static Canvas canvas = new Canvas();
	private static HashMap<String, RectF> sizeCache = new HashMap<>();
	private int size;
	private String text;

	public RenderedText( int size ){
		text = null;
		this.size = size;
	}

	public RenderedText(String text, int size){
		this.text = text;
		this.size = size;

		render();
	}

	public void text( String text ){
		this.text = text;

		render();
	}

	public String text(){
		return text;
	}

	public float baseLine(){
		return size * scale.y;
	}

	private void render(){
		if (text == null || text == "") {
			text = "";
			width=height=0;
			visible = false;
			return;
		} else {
			visible = true;
		}

		if (TextureCache.contains("text:" + size + " " + text)){
			texture = TextureCache.get("text:" + size + " " + text);
			frame(sizeCache.get("text:" + size + " " + text));
		} else {

			Paint strokePaint = new Paint();
			strokePaint.setARGB(0xff, 0, 0, 0);
			strokePaint.setTextSize(size);
			strokePaint.setStyle(Paint.Style.STROKE);
			strokePaint.setAntiAlias(true);
			strokePaint.setStrokeWidth(size / 5);

			Paint textPaint = new Paint();
			textPaint.setTextSize(size);
			textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
			textPaint.setAntiAlias(true);

			int right = (int)(strokePaint.measureText(text)+ (size/10));
			int bottom = (int)(-strokePaint.ascent() + strokePaint.descent()+ (size/10));
			Bitmap bitmap = Bitmap.createBitmap(right, bottom, Bitmap.Config.ARGB_4444);
			bitmap.eraseColor(0x00000000);

			canvas.setBitmap(bitmap);
			canvas.drawText(text, 0, Math.round(size * 0.85f), strokePaint);
			canvas.drawText(text, 0, Math.round(size * 0.85f), textPaint);
			texture = new SmartTexture(bitmap);
			TextureCache.add("text:" + size + " " + text, texture);

			RectF rect = texture.uvRect(0, 0, right, bottom);
			sizeCache.put("text:" + size + " " + text, rect);
			frame(rect);
		}
	}
}