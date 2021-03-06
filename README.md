# Mobile-Computing
This repo contains projects from the course CSE 535 : Mobile Computing.


## Cordial Care
An android application that collects COVID-19 related symptoms and stores them in a database in the smartphone. 

It has two screens. 
The first screen should present the user with two sign measurement technique: a) heart rate sensing, and b) respiratory rate sensing. 
We will use the following methods for each sensing operation.
### Heart rate sensing: 
For heart rate sensing we will utilize the back camera of the smartphone with flash enabled. We will take 45 s video from the back camera with the flash on. While taking the video the user should softly press their index finger on the camera lens while covering the flash light. From the variation of the red coloration in the image we will derive the heart rate of the subject.
### Respiratory rate sensing: 
For respiratory rate sensing we will utilize the accelerometer or orientation sensor of the smartphone. The user will be asked to lay down and place the smartphone on their chest for a period of 45 seconds. The respiratory rate will be computed from the accelerometer or orientation data. 

The user will be asked to click on the measure heart rate button and measure respiratory rate button to collect data from the smartphone sensors. The numbers will be stored in a database corresponding to the user. In this assignment we will assume only a single user. The user will then hit upload signs button which will create a database with the user’s lastname in the smartphone. The entry of the database will be a table with the first two columns heart rate and respiratory rate respectively. Each entry of the database will have 10 additional columns which will be filled in the next screen.

Once the user is done collecting signs data, the user will be taken to the second screen to collect symptoms data. The user will select a symptom and then select a rating out of 5. The user does not need to select all the symptoms. Whichever symptoms the user has not reported will be marked with 0 rating. After this the user will click a upload symptoms button. At this point a database table entry with 12 entries will be created and stored in the database in the smartphone. 

### Project demo : https://youtu.be/pFntRaXVtU8

## SmartHome
### SmartHome Gesture ControlApplication Phase 1
Develop a mobile application with the listed functionalities: 
A.The user is shown a video of a gesture. 
B.The user can replay the video at least 3 times. 
C.Upon clicking the “PRACTICE” button, the user can capture his or her own video throughthe smartphone’s front camera for a period of at most 5 seconds. 
D.The videos are uploaded to a server.

The mobile application should have three (3) screens:

1.Screen 1:​ A drop-down menu of 17 different gestures will be shown on this screen.Once a single gesture is selected, the user will be taken to Screen 2. 

Gesture list: {Turn on lights, Turn off lights, Turn on fan, Turn off fan, Increase fanspeed, decrease fan speed, Set Thermostat to specified temperature, gesturesone for each digit 0,1,2,3,4,5,6,7,8,9}

2.Screen 2:​ The video of an expert performing the gesture will be shown on this screen.Screen 2 will have another button that says “PRACTICE”. Once this button is pressed,the user will be taken to Screen 3.

3.Screen 3:​ In this screen, the camera interface will be opened for the user to record thepractice gesture. The video will be captured for ​5 seconds​, and the video will be savedwith the following filename format: [GESTURE NAME]PRACTICE[practice number].mp4

#### Project demo : https://youtu.be/48YS9C10vRM


### SmartHome Gesture ControlApplication Phase 2
A python application classifying Smart Home gestures using CNN model.

The practice gesture videos generated in project Part 1, test gesture videos provided in the test.zip in the instructions and the source code provided will be used to complete the project. 

#### Functionality of the application
##### Task 1:  Generate the penultimate layer for the training videos.
Steps to generate the penultimate layer for the training set:
1.Extract the middle frames of all the training gesture videos.
2.For each gesture video, you will have one frame extract the hand shape feature by calling theget_Intsance() method of the HandShapeFeatureExtractor class. (HandShapeFeatureExtractor classuses CNN model that is trained for alphabet gestures)3.For each gesture, extract the feature vector.4.Feature vectors of all the gestures is the penultimate layer of the training set.
##### Task 2:  Generate the penultimate layer for the test videosFollow the steps for Task 1 to get the penultimate layer of the test dataset.
##### Task 3: Gesture recognition of the test dataset.
Steps:
1.Apply cosine similarity between the vector of the gesture video and the penultimate layer of thetraining set. Corresponding gesture of the training set vector with minimum cosine difference is therecognition of the gesture.
2.Save the gesture number to the Results.csv3.Recognize the gestures for all the test dataset videos and save the results to the results.csv file.

| First Header  | Second Header |
| ------------- | ------------- |
| 0  | 0  |
| 1  | 1  |
| 2  | 2  |
| 3  | 3  |
| 4  | 4  |
| 5  | 5  |
| 6  | 6  |
| 7  | 7  |
| 8  | 8  |
| 9  | 9  |
| Decrease Fan Speed  | 10  |
| FanOn  | 11  |
| FanOff  | 12  |
| Increase Fan Speed  | 13  |
| LightOff  | 14  |
| LightOn  | 15  |
| SetThermo  | 16  |

## Biometric Liveness Detector
A mobile application that acts as a verification server for liveness detectionfor brain signals. Data will be sent to app on the phone through some communication channel (Internet or Bluetooth), the app will analyze it (machine learning techniques)and announce the result (live or not live). 

By  liveness  detection,  we  mean  that  input  should have  two properties: 1)belong to a live human being (not artificially generated), and 2) being captured  at  the  current  point  in  time  (notbeing  prerecorded). The  project  will  be focused on ensuring the first properties of liveness. The project will be focused on the first feature of liveness (live input that is not artificially generated). 








