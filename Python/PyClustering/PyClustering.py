import numpy as np
import matplotlib.pyplot as plt
from matplotlib import mlab
import Pycluster as pc
from pylab import plot,show,scatter
from os import walk, path
#from timeit import Timer

def clustering(filepath, k, dist_measure, PLOT):
    
    data = np.genfromtxt(filepath, delimiter=',')

##    print data

    if len(data.shape) == 1:
        return [-1];
    
    print "-- Processing file: " + filepath + "  -- Data points: " + str(len(data))
    print "-- Start clustering"
    if k == -1:
        k = int(0.5 * len(data))

    if k > 5000:
        k = 5000

    if len(data) < 1000:
        ite_num = 10
    else:
        ite_num = 1

    clusterid, error, nfound = pc.kcluster(data, nclusters=k, mask=None, weight=None, transpose=0, npass=ite_num, method='a', dist=dist_measure, initialid=None)

    if PLOT is False:
        return clusterid

    centroids, _ = pc.clustercentroids(data, clusterid=clusterid)
    
##    # make a plot
##    colors = ['red', 'green', 'blue']
##    plt.figure()
##    plt.xlim([data[:,0].min() - .5, data[:,0].max() + .5])
##    plt.ylim([data[:,1].min() - .5, data[:,1].max() + .5])
##    plt.xticks([], []); plt.yticks([], []) # numbers aren't meaningful
##
##    # show the centroids
##    plt.scatter(centroids[:,0], centroids[:,1], marker='o', c=colors, s=100)
##
##    # show user numbers, colored by their cluster id
##    for i, ((x,y), kls) in enumerate(zip(data, clusterid)):
##        #
##        plt.annotate('o', xy=(x,y), xytext=(0,0), textcoords='offset points', color=colors[kls])

    if PLOT:
        data_pca = mlab.PCA(data)
        cutoff = data_pca.fracs[1]
        data_2d = data_pca.project(data, minfrac=cutoff)
        centroids_2d = data_pca.project(centroids, minfrac=cutoff)
    else:
        data_2d = data
        centroids_2d = centroids
        

    color = ['#2200CC' ,'#D9007E' ,'#FF6600' ,'#FFCC00' ,'#ACE600' ,'#0099CC' ,
    '#8900CC' ,'#FF0000' ,'#FF9900' ,'#FFFF00' ,'#00CC01' ,'#0055CC']

    for i in range(k):
        scatter(data_2d[clusterid==i,0],data_2d[clusterid==i,1], color=color[i%12])
        
    plot(centroids_2d[:,0],centroids_2d[:,1],'sg',markersize=8)
    show()

    return clusterid

def wrapper(mypath, k, dist):
    #mypath = r"C:\Users\D062988\Documents\DS\clustering008\CSV"
    files = []
    dirname = ""
    for (dirpath, dirnames, filenames) in walk(mypath):
        dirname = dirpath
        files.extend(filenames)
        break

    for filename in files:
        idx = clustering(mypath + "\\" + filename, k, dist, False)
        
        print "-- Write to output file"
        out = ','.join([str(i) for i in idx])
        parent = path.dirname(path.abspath(mypath))
        f = open(parent + "\\results_C_clustering\\" + filename.split(".")[0] + ".clusters",'w')
        f.write(out)
        f.close()
        print "\n"
        

if __name__=='__main__':
    wrapper(r"C:\Users\D062988\Documents\DS\clustering008\CSV", -1, 'b')
    #wrapper(r"C:\Users\D062988\Documents\DS\clustering008\ttt\fff", -1, 'b')
##    from timeit import Timer
##    t = Timer("test()", "from __main__ import test")
##    print t.timeit(number=1)
    
    
