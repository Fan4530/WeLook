<h3 id="introduction">Introduction</h3>
This project is for users to post events, search events nearby, comment and give a like to to others.

Click <a href="https://fanzhang4530com.files.wordpress.com/2017/09/welook.mp4">Demo </a>to see the demo.

Here is the <a href="https://github.com/Fan4530/WeLook">code</a>.
<ul>
	<li>Login activity
<ul>
	<li>In this activity, users can register a new account or login with the existed account.</li>
	<li>This activity has integrated advertisement.</li>
</ul>
</li>
	<li>Event activity : for user to post event and search event
<ul>
	<li>In report fragment, users can input the description, title and location and select picture for a event and click report to post it to the network.</li>
	<li>In search fragment, users can search the events nearby and comment, give a like to a event.</li>
	<li>In the search fragment, user friendly advertisements have been integrated.</li>
</ul>
</li>
	<li>comment activity:
<ul>
	<li>this activity is for user to post comment for a event. Users can also give a like to the selected event.</li>
</ul>
</li>
</ul>
All the data: including the user information, event and comment are stored in Firebase database and the images are stored in the Firebase storage.

[wpvideo plic9rEk]
<h3 id="requirements">Requirements</h3>
<ul>
	<li>no specific requirement</li>
</ul>
<h3 id="installation">Installation</h3>
<ul>
	<li>Install Android studio</li>
</ul>
<h3 id="modules">Recommended modules</h3>
 
<h3 id="configuration">Configuration</h3>
<ul>
	<li>add the following code to build.gradle</li>
	<li>
<pre>apply plugin: 'com.android.application'
android {
    compileSdkVersion 25
    buildToolsVersion "26.0.0"
    defaultConfig {
        applicationId "com.laioffer.eventreporter"
        minSdkVersion 23//must lower than the version of machine ?? what is machine
        targetSdkVersion 25
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })



    compile 'com.android.support:design:25.1.0'
    compile 'com.android.support.constraint:constraint-layout:1.0.2'
    compile 'com.google.firebase:firebase-database:9.6.0'
    compile 'com.google.firebase:firebase-auth:9.6.0'
    compile 'com.google.firebase:firebase-storage:9.6.0'
    compile 'com.google.firebase:firebase-ads:9.6.0'
    compile 'com.google.android.gms:play-services-maps:9.6.0'
    compile 'com.android.support:appcompat-v7:25.3.1'
    testCompile 'junit:junit:4.12'
}
apply plugin: 'com.google.gms.google-services'

</pre>
</li>
</ul>
