# -*- encoding: utf-8 -*-
__author__ = 'Feng Wang'

import numpy as np
from matplotlib import mlab
import Pycluster as pc
import pylab
from os import walk, path
import collections
from operator import itemgetter

def clusteringPlot(dataFile, clusterFile, outputPath):
    time = dataFile.split("\\")
    time = time[len(time) - 1]
    time = time.split(".")
    time = time[0]

    data = np.genfromtxt(dataFile, delimiter=',')
    clusterid = np.genfromtxt(clusterFile, delimiter=',')

    if len(data.shape) == 1:
        return [-1];

    #clusterid.astype(np.int64)

    counter = collections.Counter(clusterid)
    sortedCounter = sorted(counter.items(), key=itemgetter(1), reverse = True)

    usedClusters = [np.asscalar(np.int16(i[0])) for i in sortedCounter]
    freq = [np.asscalar(np.int16(i[1])) for i in sortedCounter]

    fig1 = pylab.figure(num=None, figsize=(12, 6), dpi=80, facecolor='w', edgecolor='k')
    #rects1 = pylab.bar(ind + 0.05, freq, 0.1, color='#2200CC')
    #print freq
    pylab.xlim(-len(freq)/50, len(freq)+len(freq)/50)
    pylab.ylim(0, max(freq) + 1)
    pylab.plot(freq, marker = '.', markersize = 4)
    pylab.xlabel("Ranked clusters")
    pylab.ylabel("Number of points in the cluster")
    pylab.title("Figure of clusters points distribution in " + time)
    #pylab.show()
    fig1.savefig(outputPath + "\\" + time + '_cluster_stat.png',dpi=80)
    pylab.close()

    k = 10
    if len(sortedCounter) < k:
        k = len(sortedCounter)

    usedClusters = usedClusters[0:k]

    centroids, _ = pc.clustercentroids(data, clusterid=clusterid)

    if len(data) < len(data[0]):
        print len(data)
        print len(data[0])
        return;
    data_pca = mlab.PCA(data)
    cutoff = data_pca.fracs[1]
    data_2d = data_pca.project(data, minfrac=cutoff)
    centroids_2d = data_pca.project(centroids, minfrac=cutoff)

    color = ['#2200CC', '#D9007E', '#660066', '#FFFF00', '#FF6600', '#0099CC',
    '#8900CC', '#140A00', '#6B6B47', '#66FF66', '#FF99CC', '#0055CC']

    fig2 = pylab.figure(num=None, figsize=(12, 6), dpi=80, facecolor='w', edgecolor='k')

    Legend = []
    num=0
    for i in usedClusters:
        temp = pylab.scatter(data_2d[clusterid==i,0],data_2d[clusterid==i,1], color=color[num%12])
        Legend.append(temp)
        num += 1

    pylab.plot(centroids_2d[usedClusters,0], centroids_2d[usedClusters,1], 'sg', markersize=8)
    pylab.legend(Legend, range(1, k + 1))
    pylab.xlabel("Feature 1")
    pylab.ylabel("Feature 2")
    pylab.title("Top " + str(k) + " clusters in " + time)

    #pylab.show()

    fig2.savefig(outputPath + "\\" + time + '_cluster_plot.png',dpi=80)
    #fig2.savefig('test222png.png',dpi=80)
    pylab.close()


if __name__=='__main__':
    clusteringPlot(r"D:\198401.csv",
                   r"D:\198401.clusters")
    #wrapper(r"C:\Users\D062988\Documents\DS\clustering008\CSV", -1, 'b')
    #wrapper(r"C:\Users\D062988\Documents\DS\clustering008\ttt\fff", -1, 'b')
##    from timeit import Timer
##    t = Timer("test()", "from __main__ import test")
##    print t.timeit(number=1)