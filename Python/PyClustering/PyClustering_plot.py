# -*- encoding: utf-8 -*-
__author__ = 'Feng Wang'

import numpy as np
#import matplotlib.pyplot as plt
from matplotlib import mlab
import Pycluster as pc
from pylab import plot,show,scatter
from os import walk, path
#from timeit import Timer
from ClusteringPlot import clusteringPlot

def wrapper(dataPath, clusterPath, outputPath):
    files = []

    for (dirpath, dirnames, filenames) in walk(dataPath):
        files.extend(filenames)
        break

    for filename in files:
        print dataPath + "\\" + filename
        #print clusterPath + "\\" + filename[:len(filename) - 4] + ".clusters"

        clusteringPlot(dataPath + "\\" + filename,
                        clusterPath + "\\" + filename[:len(filename) - 4] + ".clusters",
                        outputPath)
        #
        # print "-- Write to output file"
        # out = ','.join([str(i) for i in idx])
        # parent = path.dirname(path.abspath(mypath))
        # f = open(parent + "\\results_C_clustering\\" + filename.split(".")[0] + ".clusters",'w')
        # f.write(out)
        # f.close()
        # print "\n"
        

if __name__=='__main__':
    wrapper(u"C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008\\CSV",
            u"C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008\\results_C_clustering",
            u"C:\\Users\\wdwind\\SkyDrive\\文档\\clustering008\\figures")
    #wrapper(r"C:\Users\D062988\Documents\DS\clustering008\ttt\fff", -1, 'b')
##    from timeit import Timer
##    t = Timer("test()", "from __main__ import test")
##    print t.timeit(number=1)
    
    
