import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from itertools import groupby
from xgboost import XGBClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.naive_bayes import GaussianNB

from sklearn.tree import DecisionTreeClassifier

# model = DecisionTreeClassifier()
model = XGBClassifier()


trainingDataNumber = 90000
testDataNumber = 10000
classNumber = 2

dfTrain = pd.read_csv("transactions.csv", nrows=trainingDataNumber+1, parse_dates=['Date'])
dfTest = pd.read_csv('transactions.csv', skiprows=range(1, trainingDataNumber), nrows=testDataNumber, parse_dates=['Date'])

''' Features
# CardID,
# TerminalID,
# Amount,
# Balance,
# Remaining,
# Validity,
# Date,
# CardType,
# CardLimit,
# CardLocation,
# TerminalType,
# TerminalLocation,
# MerchantName,
# NoFraud
'''

dfTest['day'] = dfTest['Date'].dt.day.astype('uint8')
dfTest['hour'] = dfTest['Date'].dt.hour.astype('uint8')

dfTrain['day'] = dfTrain['Date'].dt.day.astype('uint8')
dfTrain['hour'] = dfTrain['Date'].dt.hour.astype('uint8')

notFraudulentTrain = np.array(dfTrain['NoFraud'])
notFraudulentTest = np.array(dfTest['NoFraud'])

variables = ['CardID', 'TerminalID', 'Amount', 'day', 'hour', 'CardLocation']

'''
variables = ['CardID', 'TerminalID', 'Amount', 'Balance', 'Remaining', 'Validity', 'day', 'hour', 'CardType',
             'CardLimit', 'CardLocation', 'TerminalType', 'TerminalLocation', 'MerchantName']
'''

mapping = {'A': 1, 'B': 2, 'C': 3, 'D': 4, 'E': 5, 'POS': 0, 'ATM': 1,
           'Internet': 85, 'International': 86, 'Credit': 0, 'Debit': 1}

dfTrain = dfTrain.replace(mapping)
dfTest = dfTest.replace(mapping)


'''
clfGauss = GaussianNB()
clfGauss = clfGauss.fit(dfTrain[variables], notFraudulentTrain)
trainPredictions = clfGauss.predict_proba(dfTrain[variables])
testPredictions = clfGauss.predict_proba(dfTest[variables])
dfTrain['predictionsP0'] = trainPredictions[:,0]
dfTrain['predictionsP1'] = trainPredictions[:,1]
dfTest['predictionsP0'] = testPredictions[:,0]
dfTest['predictionsP1'] = testPredictions[:,1]

variables.extend(['predictionsP0', 'predictionsP1'])
'''

dfTrain = np.array(dfTrain[variables])
dfTest = np.array(dfTest[variables])


model = model.fit(dfTrain, notFraudulentTrain)
predictions = model.predict(dfTest)

correctClassifications = 0
for x in range(0, predictions.shape[0]):
    if predictions[x] == notFraudulentTest[x]:
        correctClassifications += 1

correctRatio = correctClassifications / testDataNumber

print("Naive bias test data accuracy: ")
print(float("%0.3f" % (100 * correctRatio)), "%")
print("Naive bias test data overall error: ")
print(float("%0.3f" % (100 * (1 - correctRatio))), "%")

freq = np.array([len(list(group)) for key, group in groupby(notFraudulentTest)])

# confusion matrix calculation
confusionMatrix = np.zeros((classNumber,classNumber));
for x in range(0,testDataNumber):
    prediction = int(predictions[x]);
    actual = int(notFraudulentTest[x]);
    confusionMatrix[actual,prediction] = confusionMatrix[actual,prediction] + 1/freq[actual];

# plot confusion matrix
fig, ax1 = plt.subplots(1,1)
ax1.imshow(confusionMatrix, cmap='Blues', alpha=.9,interpolation='nearest')
ax1.set_xticks(np.arange(0, classNumber, 1))
ax1.set_yticks(np.arange(0, classNumber, 1))
fig.suptitle("Confusion Matrix")
fig.savefig("Confusion Matrix")
plt.show()