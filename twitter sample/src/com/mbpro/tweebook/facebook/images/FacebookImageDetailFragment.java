/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.mbpro.tweebook.facebook.images;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.images.util.ImageWorker;
import com.mbpro.tweebook.facebook.images.util.Utils;
import com.mbpro.tweebook.images.util.TouchImageView;

/**
 * This fragment will populate the children of the ViewPager from {@link FacebookImageDetailActivity}.
 */
public class FacebookImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "resId";
    private int mImageNum;
    private TouchImageView mImageView;
    private ImageWorker mImageWorker;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageNum The image number within the parent adapter to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FacebookImageDetailFragment newInstance(int imageNum) {
        final FacebookImageDetailFragment f = new FacebookImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public FacebookImageDetailFragment() {}

    /**
     * Populate image number from extra, use the convenience factory method
     * {@link FacebookImageDetailFragment#newInstance(int)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.facebook_album_image_detail_fragment, container, false);
        mImageView = (TouchImageView) v.findViewById(R.id.imageView);
       /* mImageView.setOnTouchListener(new OnTouchListener() {
            private static final String TAG = "SlideImageView";
            // These matrices will be used to move and zoom image
            Matrix matrix = new Matrix();
            Matrix savedMatrix = new Matrix();

            // We can be in one of these 3 states
            static final int NONE = 0;
            static final int DRAG = 1;
            static final int ZOOM = 2;
            int mode = NONE;

            // Remember some things for zooming
            PointF start = new PointF();
            PointF mid = new PointF();
            float oldDist = 1f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;

                // Dump touch event to log
                dumpEvent(event);

                // Handle touch events here...
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    Log.d(TAG, "mode=DRAG");
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    Log.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        Log.d(TAG, "mode=ZOOM");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    Log.d(TAG, "mode=NONE");
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x,
                                event.getY() - start.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        Log.d(TAG, "newDist=" + newDist);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            Log.d(TAG, "ZOOOOOOOM: " + scale);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
                }

                view.setImageMatrix(matrix);
                return true; // indicate event was handled
            }

            *//** Show an event in the LogCat view, for debugging *//*
            private void dumpEvent(MotionEvent event) {
                String names[] = { "DOWN", "UP", "MOVE", "CANCEL",
                        "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?",
                        "8?", "9?" };
                StringBuilder sb = new StringBuilder();
                int action = event.getAction();
                int actionCode = action & MotionEvent.ACTION_MASK;
                sb.append("event ACTION_").append(names[actionCode]);
                if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                        || actionCode == MotionEvent.ACTION_POINTER_UP) {
                    sb.append("(pid ").append(
                            action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                    sb.append(")");
                }
                sb.append("[");
                for (int i = 0; i < event.getPointerCount(); i++) {
                    sb.append("#").append(i);
                    sb.append("(pid ").append(event.getPointerId(i));
                    sb.append(")=").append((int) event.getX(i));
                    sb.append(",").append((int) event.getY(i));
                    if (i + 1 < event.getPointerCount())
                        sb.append(";");
                }
                sb.append("]");
                Log.d(TAG, sb.toString());
            }

            *//** Determine the space between the first two fingers *//*
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return FloatMath.sqrt(x * x + y * y);
            }

            *//** Calculate the mid point of the first two fingers *//*
            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });
*/
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (FacebookImageDetailActivity.class.isInstance(getActivity())) {
            mImageWorker = ((FacebookImageDetailActivity) getActivity()).getImageWorker();
            mImageWorker.loadImage(mImageNum, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasActionBar()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    /**
     * Cancels the asynchronous work taking place on the ImageView, called by the adapter backing
     * the ViewPager when the child is destroyed.
     */
    public void cancelWork() {
        ImageWorker.cancelWork(mImageView);
        mImageView.setImageDrawable(null);
        mImageView = null;
    }
}
