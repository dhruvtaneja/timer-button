# Timer Button

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-timer--button-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7065)

Timer button is a countdown enabled button which can be used to disable user interactions while showing
a timer on top of it.


![](https://dl2.pushbulletusercontent.com/FC3WGsT0Z3zWRJVyV7rjdehhQ1SLzEQM/Screen%20Shot%202018-07-14%20at%2019.17.05.png)


### Gradle
To include this library in your gradle project
```groovy
dependencies {
	// ... other dependencies here
    implementation 'com.dt:timerbutton:0.1'
}
``` 

### Maven
To include this library in your maven project
```xml
<dependency>
  <groupId>com.dt</groupId>
  <artifactId>timerbutton</artifactId>
  <version>0.1</version>
  <type>pom</type>
</dependency>
```

## Usage

Put this button in your layout file like this

```xml
<com.dhruv.timerbutton.TimerButton
        android:id="@+id/timer_button"
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        app:animationBackground="@color/colorAccentTrans"
        app:animationCompleteText="@string/resend_otp"
        app:buttonBackground="@drawable/selector_button"
        app:defaultText="@string/send_otp"
        app:dynamicString="@string/resend_otp_formatted"
        app:textColor="@color/colorPrimaryDark"/>
```

The various attributes that are available are

```xml
<declare-styleable name="TimerButton">
        <!--text to be displayed after animation is complete-->
        <attr name="animationCompleteText" format="string|reference"/>
        <!--text to be displayed in pre-animation state-->
        <attr name="defaultText" format="string|reference"/>
        <!--text to be displayed during animation-->
        <attr name="dynamicString" format="reference"/>
        <!--the overlay that animates over the button. Can be a drawable, a gradient, etc-->
        <attr name="animationBackground" format="reference"/>
        <!--the background of the button-->
        <attr name="buttonBackground" format="reference"/>
        <!--color of the text-->
        <attr name="textColor" format="reference|color"/>
        <!--size of the text-->
        <attr name="textSize" format="reference|dimension"/>
    </declare-styleable>
```

Now in your java code, start, stop or reset the animation with something like this

```java
TimerButton timerButton = findViewById(R.id.timer_button);
timerButton.setDuration(6000L);

//  Start the animation
timerButton.start();

//  Stop the animation
timerButton.stop();

//  Reset the animation
timerButton.reset();
```

**Issues and pull requests are most welcome**
