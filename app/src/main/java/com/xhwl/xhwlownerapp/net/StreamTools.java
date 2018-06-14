package com.xhwl.xhwlownerapp.net;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamTools {

		public static String readStream(InputStream in) throws Exception {
			
			//
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[1024]; //1kb
			while((len=in.read(buffer))!=-1){

				baos.write(buffer, 0, len);
			}
			in.close();
			String content = new String(baos.toByteArray(),"utf-8");

			return content;
			
		}

	public static String getJson(Context mContext, String fileName) {

		StringBuilder sb = new StringBuilder();
		AssetManager am = mContext.getAssets();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					am.open(fileName)));
			String next = "";
			while (null != (next = br.readLine())) {
				sb.append(next);
			}
		} catch (IOException e) {
			Log.e("读取json文件","异常");
			e.printStackTrace();
			sb.delete(0, sb.length());
		}
		return sb.toString().trim();
	}
}
