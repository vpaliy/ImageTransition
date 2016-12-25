# ImageTransition
[![API](https://img.shields.io/badge/API-12%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=12) 

This project provides a library for making a transitions between activities with shared images.

# Simple example #
![](https://github.com/vpaliyX/ImageTransition/blob/master/art/ezgif.com-video-to-gif%20(4).gif)

Please check out a [video](https://www.youtube.com/watch?v=ybzTDJHUrSo) with the same sample, there it goes more smoothly.

# How do I use a shared image transition? #

Basically, there are only three main abstractions you need to deal with: `TransitionStarter`, `TransitionRunner` and `TransitionAnimation`.

For instance, you have 2 activities `A` and `B`, and both of them share the same image resource.

 * First of all, you need to make `B` transparent:
 
    ```xml
     <style name="Transparent" parent="AppTheme">
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
     </style>
    ```

* Launch `B` activity in `A` using `TransitionStarter`.

```java
  private void launchB(@NonNull File mediaFile, @NonNull ImageView sharedImage) {
        /*At this point you can send any data you want, there are no restrictions*/
        Intent intent=new Intent(this,ActivityB.class);
        intent.putExtra("pathToImage",mediaFile.getAbsolutePath());
        
        TransitionStarter.with(this).from(sharedImage).start(intent); //that's it!
    }
 ```
 
 * In 'B' activity you need to catch and process the data using `TransitionRunner` thus you will start the animation.
 
    However, at this point you may want to use some library that will prepare your `ImageView`, 
    it could be `Picasso`, `Glide`, `Volley`, `ImageLoader` or `Fresco` (My own preference is Glide).
    So you need to make sure that the image is fetched before starting any transition. In order to ensure that the image has been 
     fetched you can use some kind of callback. For example, in `Glide` you can use the following code:
 ```java
      Glide.with(this)
                    .load(resource)
                    .asBitmap()
                    .thumbnail(0.2f)    //to make the loading of image faster
                    .centerCrop()
                    //to prevent the animation from shuddering, use the listener to track when the image is ready,
                    // otherwise you may start the animation when the resource hasn't been loaded yet
                    .listener(new RequestListener<File, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, File model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        //when the image has been loaded, start the transition
                        @Override
                        public boolean onResourceReady(Bitmap resource, File model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(isFirstResource) {
                                startTransition(getIntent().getExtras(),image); //pass the data and `ImageView`
                            }
                            return false; // please return false, do not return true
                        }
                    })
                    .into(image);
 ```
 
 * Finally, after the resource has been loaded into `ImageView`, you can use `TransitionRunner` and run the transition.
 
  ```java
      private void startTransition(Bundle state, ImageView targetImage){
           //in order to animate transition backwards when an activity finishes, save created instance as global variable
           runner = TransitionRunner.with(state).target(image);
           runner.run(TransitionAnimation.ENTER); //that's it!
      }
  ```
  
  * Also you can specify transition duration, interpolators, and add listeners.
      This example shows animating of the background of root view.
      So, you can run any other animations at the same time with image transition.
      
  ```java
      private void startTransition(Bundle state, ImageView targetImage){
           //in order to animate the transition backwards when an activity finishes, save created instance as global variable
           runner = TransitionRunner.with(state).target(image).addListener(new TransitionListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                ViewGroup parent=(ViewGroup)(image.getParent());
                ColorDrawable background=new ColorDrawable(Color.BLACK);
                parent.setBackgroundDrawable(background);
                ObjectAnimator backgroundAnimator=ObjectAnimator.ofInt(background,"alpha",0,255);
                backgroundAnimator.setDuration(animator.getDuration());
                backgroundAnimator.setInterpolator(new DecelerateInterpolator());
                backgroundAnimator.start();
            }
        }).duration(500).interolator(new DecelerateInterpolator());
        runner.run(TransitionAnimation.ENTER); //that's it!
      }
  ```
  
 * Once the activity finishes, you need to animate the image backwards.
     In this case you need to use `TransitionAnimation.EXIT` instance.
     
     For example, when the user clicks on the back button, you may use the code below in order to return back:
    ```java
         @Override
         public void onBackPressed() {           
            //if the transition has occurred, go ahead and start animating the image backwards
            if(runner!=null) {
                //if you have used any listeners before, clear that 
                runner.clearListeners();  
                runner.addListener(new TransitionListener() {
                   //in the end of animation you need to finish the current activity and return back to the caller
                    @Override
                    public void onAnimationEnd(Animator animator) {
                       super.onAnimationEnd(animator);
                       //finish activity
                       finish();
                       
                       //disable default transitions 
                       overridePendingTransition(0,0);
                    }
                });
                runner.run(TransitionAnimation.EXIT);
            }
        }
   ```

## License ##

``````
MIT License

Copyright (c) 2016 Vasyl Paliy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
``````
