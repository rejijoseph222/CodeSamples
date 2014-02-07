package com.mbpro.tweebook.facebook.images;

import com.mbpro.tweebook.facebook.images.util.ImageWorker.ImageWorkerAdapter;

public class FaceBookAlbumImages {
	public  static String[] albumImageUrls;
	public  static String[] albumImageThumbUrls;
	 /**
     * Simple static adapter to use for images.
     */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return FaceBookAlbumImages.albumImageUrls[num];
        }

        @Override
        public int getSize() {
            return FaceBookAlbumImages.albumImageUrls.length;
        }
    };

    /**
     * Simple static adapter to use for image thumbnails.
     */
    public final static ImageWorkerAdapter imageThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return FaceBookAlbumImages.albumImageThumbUrls[num];
        }

        @Override
        public int getSize() {
            return FaceBookAlbumImages.albumImageThumbUrls.length;
        }
    };
}
