# -*- coding: utf-8 -*-

__author__ = 'Feng Wang'

from pylab import plot, show
from numpy import vstack, array, genfromtxt, zeros, genfromtxt
from scipy.cluster.vq import kmeans, vq, whiten
from random import random

# data generation
#data = vstack((rand(150,2) + array([.5,.5]),rand(150,2)))
##data = array([[0.1,   2.5],
##              [1.5,   .4 ],
##              [0.3,   1  ],
##              [1  ,   .8 ],
##              [0.5,   0  ],
##              [0  ,   0.5],
##              [0.5,   0.5],
##              [2.7,   2  ],
##              [2.2,   3.1],
##              [3  ,   2  ],
##              [3.2,   1.3]])
#data = genfromtxt('randomData.csv', delimiter=',')

# i1 = (1000000, 20)
# data = zeros(i1, dtype=int)
#
# for ind_r in range(0, len(data)):
#     for ind_c in range(0, 20):
#         data[ind_r][ind_c] = (random() < 0.5)

data = genfromtxt('001205.csv', delimiter=',')
print len(data)

data = whiten(data)

# computing K-Means with K = 2 (2 clusters)
centroids,_ = kmeans(data, 200)
print centroids[10]
# assign each sample to a cluster
idx,_ = vq(data,centroids)
print idx
print len(idx)

# some plotting using numpy's logical indexing
plot(data[idx == 0, 0], data[idx == 0, 1], 'ob',
     data[idx == 1, 0], data[idx == 1, 1], 'or')
plot(centroids[:, 0], centroids[:, 1], 'sg', markersize=8)
show()

