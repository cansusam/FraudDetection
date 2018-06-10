import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn import model_selection
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis, QuadraticDiscriminantAnalysis
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.neural_network import MLPClassifier
from xgboost import XGBClassifier
import lightgbm as lgb


trainingDataNumber = 100000
dfTrain = pd.read_csv("transactions.csv", parse_dates=['Date'], nrows=trainingDataNumber)

dfTrain['day'] = dfTrain['Date'].dt.day.astype('uint8')
dfTrain['hour'] = dfTrain['Date'].dt.hour.astype('uint8')

notFraudulentTrain = np.array(dfTrain['NoFraud'])

variables = ['CardID', 'TerminalID', 'Amount', 'day', 'hour']

'''
variables = ['CardID', 'TerminalID', 'Amount', 'Balance', 'Remaining', 'Validity', 'day', 'hour', 'CardType',
             'CardLimit', 'CardLocation', 'TerminalType', 'TerminalLocation', 'MerchantName']
'''

mapping = {'A': 1, 'B': 2, 'C': 3, 'D': 4, 'E': 5, 'POS': 0, 'ATM': 1,
           'Internet': 85, 'International': 86, 'Credit': 0, 'Debit': 1}

dfTrain = dfTrain.replace(mapping)

dfTrain = np.array(dfTrain[variables])

features = []
features.append(variables)
# prepare configuration for cross validation test harness
seed = 7
# prepare models
models = []
models.append(('LR ', LogisticRegression()))
models.append(('LDA ', LinearDiscriminantAnalysis()))
models.append(('QDA ', QuadraticDiscriminantAnalysis()))
models.append(('CART ', DecisionTreeClassifier()))
models.append(('NB ', GaussianNB()))
models.append(('KNN ', KNeighborsClassifier()))
models.append(('MLP ', MLPClassifier()))
models.append(('XGB ', XGBClassifier()))
# models.append(('LGB ', lgb()))
# models.append(('SVC ', SVC()))

# evaluate each model in turn
results = []
names = []
scoring = 'accuracy'
for name, model in models:
    for featureSet in features:
        kfold = model_selection.KFold(n_splits=10, random_state=seed)
        cv_results = model_selection.cross_val_score(model, dfTrain, notFraudulentTrain, cv=kfold, scoring=scoring)
        results.append(cv_results)
        names.append([name])
        msg = "%s: %f (%f) %s" % (name, cv_results.mean(), cv_results.std(), featureSet)
        print(msg)
# boxplot algorithm comparison
fig = plt.figure()
fig.suptitle('Algorithm Comparison')
ax = fig.add_subplot(111)
plt.boxplot(results)
ax.set_xticklabels(names)
#plt.show()
fig.savefig('cv_results')