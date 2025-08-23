## Preview


![](https://raw.githubusercontent.com/mobarokOP/SunriseView/refs/heads/master/app/src/main/assets/img.png)



## Installation

Add to _build.gradle_:

```
implementation 'com.github.mobarokOP:SunriseView:1.0.1'
```

Add to _settings.gradle_:

``` Under ependencyResolutionManagement >> repositories
			maven { url 'https://jitpack.io' }
```


## Include SunriseView in your layout

``` xml

         <com.sunriseview.SunriseView
            android:layout_width="200dp"
            android:layout_height="wrap_content"
            app:sunrise_text_color="@color/white"
            app:sunrise_track_color="@color/white"
            app:sunrise_track_width="1.5dp"
            app:sunrise_text_size="10sp"
            app:sunrise_sun_radius="10dp"
            android:id="@+id/sunriseView"/>
```

## Load The ProgressBar

All available options with default values:
``` java
  SunriseView mSunriseSunsetView = findViewById(R.id.sunriseView);
        
         int sunriseHour = 6;
        int sunriseMinute = 17;
        int sunsetHour = 18;
        int sunsetMinute = 32;
        mSunriseSunsetView.setSunriseTime(new Time(sunriseHour, sunriseMinute));
        mSunriseSunsetView.setSunsetTime(new Time(sunsetHour, sunsetMinute));
        mSunriseSunsetView.startAnimate();

```

## One more thing
If you have any suggestions on making this lib better, write me, create issue or write some code and send pull request.

## License

Created  by [Mobarok Hossain](http://mobarokop.github.io/)
```
Copyright 2025 Mobarok Hossain

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
