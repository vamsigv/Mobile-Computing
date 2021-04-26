import shutil

import cv2

import numpy as np

from numpy import genfromtxt

import os

import tensorflow as tf

import glob

# import the handfeature extractor class

from scipy import spatial

import frameextractor
from handshape_feature_extractor import HandShapeFeatureExtractor
def getPenultimateLayer(frames_path, opfilename):
    print("pen layer")
    files = []
    path = os.path.join(frames_path, "*.png")
    frames = glob.glob(path)
    frames.sort()
    files = frames
    prediction_vector = frameextractor.get_inference_vector_one_frame_alphabet(files)
    print(prediction_vector)
    np.savetxt(opfilename, prediction_vector, delimiter=",")
def getGesture(test_vector, trainingset_penlayer):
    lst = []
    for x in trainingset_penlayer:
        lst.append(spatial.distance.cosine(test_vector, x))
        print(spatial.distance.cosine(test_vector, x))
        gesture_num = lst.index(min(lst))
        print(gesture_num)
    return gesture_num


# =============================================================================

# Get the penultimate layer for training data

# =============================================================================

# your code goes here

# Extract the middle frame of each gesture video
shutil.rmtree(os.path.join(os.getcwd(), "frames"))
videofiles = []
video_folder_path = os.path.join("traindata")  # os.path.join("C:\RecordedGestures")
video_path = os.path.join(video_folder_path, "*.mp4")
print(video_path)
videos = glob.glob(video_path)
videofiles = videos
count = 0
print("fetch videos")
for video in videofiles:
    frames_path = os.path.join(os.getcwd(), "frames")
    frameextractor.frameExtractor(video, frames_path, count)
    count = count + 1
opfilename1 = 'trainingset_penlayer.csv'
print(os.getcwd())
frames_path1 = os.path.join(os.getcwd(), "frames")
getPenultimateLayer(frames_path1, opfilename1)

# =============================================================================

# Get the penultimate layer for test data

# =============================================================================

# your code goes here

# Extract the middle frame of each gesture video
shutil.rmtree(os.path.join(os.getcwd(), "frames"))
videofiles = []
video_folder_path = os.path.join("test")  # os.path.join("C:\RecordedGestures")
video_path = os.path.join(video_folder_path, "*.mp4")
print(video_path)
videos = glob.glob(video_path)
videofiles = videos
count = 0
print("fetch videos")
for video in videofiles:
    frames_path = os.path.join(os.getcwd(), "frames")
    frameextractor.frameExtractor(video, frames_path, count)
    count = count + 1
opfilename2 = 'testset_penlayer.csv'
print(os.getcwd())
frames_path2 = os.path.join(os.getcwd(), "frames")
getPenultimateLayer(frames_path2, opfilename2)
training_data = genfromtxt(opfilename1, delimiter=',')
test_data = genfromtxt(opfilename2, delimiter=',')
res = []
for x in test_data:
    res.append(getGesture(x, training_data))
print(res)
np.savetxt('Results_numbers_new.csv', res, delimiter=",", fmt='% d')
# =============================================================================

# Recognize the gesture (use cosine similarity for comparing the vectors)

# =============================================================================
'''
def getGesture(test_vector, trainingset_penlayer):
    lst = []
    for x in trainingset_penlayer:
        lst.append(spatial.distance.cosine(test_vector, x))
        gesture_num = lst.index(min(lst) + 1)
    return gesture_num
'''
