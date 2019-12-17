from sklearn.datasets import  load_svmlight_file
from imblearn.over_sampling import RandomOverSampler
from imblearn.combine import SMOTEENN


x, y = load_svmlight_file(r"/home/hadoop-hch/Downloads/machine/train.txt")  #导入需要平衡的数据集
ros=RandomOverSampler(random_state=0)

smote_enn = SMOTEENN(random_state=0)
X_smote,y_smote=smote_enn.fit_sample(x,y)           #进行平衡操作
print("\n\n")

lists = zip(X_smote.toarray(),y_smote)          #用列表来存放新生成的图像数据和标签  x是一个列表，代表图像(28x28)的灰度值，y代表标签

file=open(r"/home/hadoop-hch/Downloads/machine/new.txt",'w')
LabelPoint=""
for (a,b) in lists:                     #将数据写入文件
    LabelPoint+=str(int(b))
    n=1
    for i in a:
        if i==1 and n!=784:
            LabelPoint+=" "+str(n)+":1"
        if n==784:
            LabelPoint+=" "+str(n)+":"+str(int(i))
        n+=1
    LabelPoint+="\n"
file.write(LabelPoint)
file.close()


