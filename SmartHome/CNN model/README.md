# SmartHome Gesture Control Application Phase 2
A python application classifying Smart Home gestures using CNN model.
The practice gesture videos generated in project Part 1, test gesture videos provided in the test.zip in the instructions and the source code provided will be used to complete the project.
Functionality of the application
## Task 1: Generate the penultimate layer for the training videos.
Steps to generate the penultimate layer for the training set: 
1.Extract the middle frames of all the training gesture videos. 
2.For each gesture video, you will have one frame extract the hand shape feature by calling theget_Intsance() method of the HandShapeFeatureExtractor class. (HandShapeFeatureExtractor classuses CNN model that is trained for alphabet gestures)
3.For each gesture, extract the feature vector.
4.Feature vectors of all the gestures is the penultimate layer of the training set.
## Task 2: Generate the penultimate layer for the test videosFollow the steps for Task 1 to get the penultimate layer of the test dataset.
## Task 3: Gesture recognition of the test dataset.
Steps: 
1.Apply cosine similarity between the vector of the gesture video and the penultimate layer of thetraining set. Corresponding gesture of the training set vector with minimum cosine difference is therecognition of the gesture. 
2.Save the gesture number to the Results.csv
3.Recognize the gestures for all the test dataset videos and save the results to the results.csv file.
 

#### main.py 
Contains the frame Extractor function, a function to get the penultimate layer,  and to get the gesture number. 
#### handshape_feature_extractor.py
HandShapeFeatureExtractor classuses CNN model that is trained for alphabet gestures)
#### frameextractor.py
This file contains methods to extract the middle frame and store it in a folder.
#### cnn_model.h5
This file contains the CNN model.
