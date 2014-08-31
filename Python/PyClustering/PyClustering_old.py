__author__ = 'Feng Wang'

from os import walk, path
from numpy import vstack,array,genfromtxt,zeros
from numpy.random import rand
from scipy.cluster.vq import kmeans,kmeans2,vq,whiten
from random import random
#from __future__ import print_function
#from timeit import Timer

def clustering():
    mypath = r"C:\Users\D062988\Documents\DS\clustering008\CSV"
    files = []
    dirname = ""
    for (dirpath, dirnames, filenames) in walk(mypath):
        dirname = dirpath
        files.extend(filenames)
        break


    for filename in files:
        data = genfromtxt(mypath + "\\" + filename, delimiter=',')
        print "-- Processing file: " + filename + "  -- Data points: " + str(len(data))
        print "-- Whitening data"
        data = whiten(data)
        print "-- Start clustering"
        if len(data) < 10000:
            cen_num = int(0.5 * len(data))
            ite_num = 5;
            #centroids,_ = kmeans(data, int(0.5 * len(data)))
        else:
            cen_num = int(0.1 * len(data))
            ite_num = 1;
            #centroids,_ = kmeans(data, int(0.2 * len(data)))

        if len(data) < 1000:
            ite_num = 10

        if cen_num == 0:
            cen_num = 1

        if cen_num > 10000:
            cen_num = 10000

        centroids,_ = kmeans(data, cen_num, ite_num)
        
        #print "-- End clustering"
        print "-- Assign cluster to data"
        idx,_ = vq(data,centroids)
        idx = idx.tolist()

        print "-- Write to output file"
        out = ','.join([str(i) for i in idx])
        parent = path.dirname(path.abspath(mypath))
        f = open(parent + "\\results\\" + filename.split(".")[0] + ".clusters",'w')
        f.write(out)
        f.close()
        print "\n"

if __name__=='__main__':
    clustering()
##    from timeit import Timer
##    t = Timer("test()", "from __main__ import test")
##    print t.timeit(number=1)
    
    
