package com.tifone.demo.camera.thumb;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Locale;

import static com.tifone.demo.camera.LogUtilKt.tlogd;
import static com.tifone.demo.camera.utils.StorageUtilKt.getExternalPath;

public class ThumbnailModelImpl implements ThumbnailModel {
    private ContentResolver mResolver;
    @Override
    public Bitmap getThumbnail(Context context) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mResolver = context.getContentResolver();
        ThumbMedia image = getLatestImageThumbMedia();
        ThumbMedia video = getLatestVideoThumbMedia();
        Bitmap result = null;
        if (image != null) {
            if (video == null || (image.takenDate > video.takenDate)) {
                result = MediaStore.Images.Thumbnails.getThumbnail(mResolver,
                        image.id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
        } else if (video != null) {
            result = MediaStore.Video.Thumbnails.getThumbnail(mResolver,
                    video.id, MediaStore.Video.Thumbnails.MINI_KIND, null);
        }
        return result;
    }
    private ThumbMedia getLatestImageThumbMedia() {
        // specify the external media provider uri
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // build query projection and selection
        String[] projection = new String[] { MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.ORIENTATION };
        StringBuilder selection = new StringBuilder("(");
        int bucketId = getExternalPath().toLowerCase(Locale.ENGLISH).hashCode();
        tlogd("bucketId = " + bucketId);
        selection.append(MediaStore.Images.Media.MIME_TYPE)
                .append("=")
                .append("'image/jpeg'")
                .append(" AND ")
                .append(MediaStore.Images.Media.BUCKET_ID)
                .append("=")
                .append(bucketId)
                .append(")");
        String order = MediaStore.Images.ImageColumns.DATE_TAKEN +
                " DESC, " + MediaStore.Images.ImageColumns._ID + " DESC";
        Cursor cursor = null;
        try {
            cursor = mResolver.query(external, projection,
                    selection.toString(), null, order);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                long takenDate = cursor.getLong(1);
                int orientation = cursor.getInt(2);
                cursor.close();
                // create the wrap data
                return new ThumbMedia(id, ContentUris.withAppendedId(external, id),
                        takenDate, orientation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    private ThumbMedia getLatestVideoThumbMedia() {
        Uri external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN
        };
        StringBuilder selection = new StringBuilder("(");
        int bucketId = getExternalPath().toLowerCase(Locale.ENGLISH).hashCode();
        tlogd("bucketId = " + bucketId);
        selection.append(MediaStore.Images.Media.BUCKET_ID)
                .append("=")
                .append(bucketId)
                .append(")");
        String order = MediaStore.Images.ImageColumns.DATE_TAKEN +
                " DESC, " + MediaStore.Images.ImageColumns._ID + " DESC";
        Cursor cursor = null;
        try {
            cursor = mResolver.query(external, projection,
                    selection.toString(), null, order);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                long takenDate = cursor.getLong(1);
                cursor.close();
                // create the wrap data
                return new ThumbMedia(id, ContentUris.withAppendedId(external, id),
                        takenDate, 0);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }  finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    static class ThumbMedia {
        long id;
        long takenDate;
        Uri uri;
        int orientation;
        ThumbMedia(long id, Uri uri, long takenDate, int orientation) {
            this.id = id;
            this.uri = uri;
            this.takenDate = takenDate;
            this.orientation = orientation;
        }
    }
}
