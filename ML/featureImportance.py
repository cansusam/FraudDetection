import pandas as pd
import numpy as np
import seaborn as sns
from sklearn.tree import DecisionTreeClassifier
from xgboost import XGBClassifier
import matplotlib.pyplot as plt


trainingDataNumber = 100000

dfTrain = pd.read_csv("transactions.csv", parse_dates=['Date'], nrows=trainingDataNumber)

dfTrain['day'] = dfTrain['Date'].dt.day.astype('uint8')
dfTrain['hour'] = dfTrain['Date'].dt.hour.astype('uint8')

notFraudulentTrain = np.array(dfTrain['NoFraud'])

variables = ['CardID', 'TerminalID', 'Amount', 'Balance', 'Remaining', 'Validity', 'day', 'hour', 'CardType',
             'CardLimit', 'CardLocation', 'TerminalType', 'TerminalLocation', 'MerchantName']

'''
variables = ['CardID', 'TerminalID', 'Amount', 'Balance', 'Remaining', 'Validity', 'day', 'hour', 'CardType',
             'CardLimit', 'CardLocation', 'TerminalType', 'TerminalLocation', 'MerchantName']
'''

mapping = {'A': 1, 'B': 2, 'C': 3, 'D': 4, 'E': 5, 'POS': 0, 'ATM': 1,
           'Internet': 85, 'International': 86, 'Credit': 0, 'Debit': 1}

dfTrain = dfTrain.replace(mapping)

plt.figure(figsize=(15, 8))
pal = sns.color_palette()
uniques = [len(dfTrain[col].unique()) for col in variables]
sns.set(font_scale=1.2)
ax = sns.barplot(variables, uniques, palette=pal, log=True)
ax.set(xlabel='Feature', ylabel='log(unique count)', title='Number of unique values per feature')
for p, uniq in zip(ax.patches, uniques):
    height = p.get_height()
    ax.text(p.get_x()+p.get_width()/2.,
            height + 10,
            uniq,
            ha="center")
plt.savefig('uniqueness.png')

dfTrain = np.array(dfTrain[variables])

# feature extraction
model = XGBClassifier()
model.fit(dfTrain, notFraudulentTrain)

for i in range(0, len(variables)):
    print(variables[i], model.feature_importances_[i])

plt.clf()
ax = sns.barplot(variables, model.feature_importances_, palette=pal, log=True)
ax.set(xlabel='Feature', ylabel='log(unique count)', title='Feature Importance for XGB')
for p, uniq in zip(ax.patches, model.feature_importances_):
    height = p.get_height()
    ax.text(p.get_x()+p.get_width()/2.,
            height + 10,
            uniq,
            ha="center")
plt.savefig('featureImportance_XGB.png')
