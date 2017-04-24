# ImageTransition
[![API](https://img.shields.io/badge/API-12%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=12) 

This project provides a library for making transitions between the activities with shared images.

# Simple example #
![](https://github.com/vpaliyX/ImageTransition/blob/master/art/ezgif.com-video-to-gif%20(4).gif)

Please check out a [video](https://www.youtube.com/watch?v=ybzTDJHUrSo) with the same sample, there it goes more smoothly.

# How do I use a shared element transition? #
Basically, there are only four main abstractions you need to deal with: `AnimatedImageView`,`TransitionStarter`, `TransitionRunner` and `TransitionAnimation`. If you want to implement this transition on pre-lollipop devices, you have to use `AnimatedImageView` or its subclasses, in order to share the images with different scale type, otherwise you get undesired behaviour.<br>
  Let's suppose you have two activities `A` and `B`, and you want to make a transition with a shared image from `A` activity to `B`.
  
 * First of all, you need to make `B` transparent, so the users can see where the image comes from:
 
    ```xml
     <style name="Transparent" parent="AppTheme">
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
     </style>
    ```
 * In `A` activity you need to do the following:

  ```java
    @Override
    protected void onStart() {
        super.onStart();
        Registrator.register(this);
    }
    
    @Override
    public void onStop(){
        super.onStop();
        Registrator.unregister(this);
    }
  ```
  
 * In order to change a thumbnail when the first activity calls another activity, don't forget to subscribe for and implement the following method in `A`:
 ```java
       //that's my implementation of the trigger, you can customize it
       //however, it has to have the same parameter and annotation
    @Subscribe
    public void triggerVisibility(TriggerVisibility trigger) {
        ImageView image=(ImageView)(recyclerView.
                findViewWithTag(ProjectUtils.TRANSITION_NAME(trigger.requestedPosition())));
        image.setVisibility(trigger.isVisible()?View.VISIBLE:View.INVISIBLE);
    }
 ```
 
 * Finally, launch `B` activity in `A` using `TransitionStarter`.

    ```java
      private void launchB(@NonNull File mediaFile, @NonNull ImageView sharedImage) {
            /*At this point you can send any data you want, there are no restrictions*/
            Intent intent=new Intent(this,ActivityB.class);
            intent.putExtra("pathToImage",mediaFile.getAbsolutePath());
        
            TransitionStarter.with(this).from(sharedImage).start(intent); //that's it!
        }
    ```
 
 
* In 'B' activity you need to catch and process the data using the `TransitionRunner` thus you will start the animation.
 
    However, at this point you may want to use some library that will prepare your `AnimatedImageView`, 
    it could be `Picasso`, `Glide`, `Volley`, `ImageLoader` or `Fresco`. So you need to make sure that the image is fetched before starting any transition. In order to ensure that the image has been 
     fetched you may use some kind of callback. For example, in `Glide` you can use the following code:
 ```java
      Glide.with(this)
                    .load(resource)
                    .asBitmap()
                    .centerCrop()
                    //to prevent the animation from shuddering, use the listener to track when the image is ready,
                    // otherwise you may start the animation when the resource hasn't been loaded yet
                    .into(new ImageViewTarget<Bitmap>(image) {
                       @Override
                       protected void setResource(Bitmap resource) {
                         image.setImageBitmap(resource);
                         startTransition(getIntent(),image); //pass the data and your image
                      }
                     });
                 
 ```
 
* Finally, after the resource has been loaded into the `ImageView`, you can use the`TransitionRunner` and run that transition.
 
  ```java
      private void startTransition(Intent intent, AnimatedImageView targetImage){
           //in order to animate a transition backwards when an activity finishes, save created instance as a global variable
        ViewGroup container=(ViewGroup)(image.getParent()); //pull out the container 
        runner = TransitionRunner.with(intent)
            .target(targetImage)
            .fadeContainer(container)
            .duration(200);
        runner.run(TransitionAnimation.ENTER); //apply the TransitionAnimation.ENTER in this case
      }
  ```
  
* As you can see, you are able to animate the background of your `ViewGroup`, set the duration, set the interpolators, and add listeners.
      
 ```java
      private void startTransition(Intent intent, AnimatedImageView targetImage){
           //in order to animate a transition backwards when an activity finishes, save created instance as a global variable
        ViewGroup container=(ViewGroup)(image.getParent()); //pull out the container 
        runner = TransitionRunner.with(intent).
            target(targetImage)
              .fadeContainer(container) //default color is black
              .duration(200)
              .fadeContainer(container, Color.WHITE) //set your own color
              .interpolator(new DecelerateInterpolator())
              .addListener(new TransitionListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    //add your actions
                }
               });

        runner.run(TransitionAnimation.ENTER);
      }
 ```
  
* Once the activity finishes, you need to animate the image backwards.
     In this case you need to use the `TransitionAnimation.EXIT` instance.
     
     For instance, when the user clicks on the back button, you may use the code below in order to return back to the caller:
    ```java
         @Override
         public void onBackPressed() {           
            //if the transition has occurred, go ahead and start animating the image backwards
            if(runner!=null) {
                runner.runAway(TransitionAnimation.EXIT,this);
            }
        }
   ```

# Get on Google Play #
<a href="https://play.google.com/store/apps/details?id=com.vasya.phototransition">
<img src="https://github.com/chrisbanes/PhotoView/blob/master/art/google-play-badge-small.png" />
</a>


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
