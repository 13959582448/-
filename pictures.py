# coding=utf-8
'''将二进制格式的MNIST数据集转成.jpg图片格式并保存，图片标签包含在图片名中'''
import numpy as np
import cv2
import os

def save_mnist_to_jpg(mnist_image_file, mnist_label_file, save_dir):
    if 'train' in os.path.basename(mnist_image_file):
        num_file = 60000
        prefix = 'train'
    else:
        num_file = 10000
        prefix = 'test'
    with open(mnist_image_file, 'rb') as f1:
        image_file = f1.read()
    with open(mnist_label_file, 'rb') as f2:
        label_file = f2.read()
    image_file = image_file[16:]        #从第16个字节之后才是图片的灰度值
    label_file = label_file[8:]         #从第8个字节之后才是标签的具体值
    for i in range(num_file):
        label = int(label_file[i].encode('hex'), 16)
        image_list = [int(item.encode('hex'), 16) for item in image_file[i*784:i*784+784]]      #每次读取784字节的数据，代表一个图片的所有灰度值。
        image_np = np.array(image_list, dtype=np.uint8).reshape(28,28,1)
        save_name = os.path.join(save_dir, '{}_{}_{}.jpg'.format(prefix, i, label))
        cv2.imwrite(save_name, image_np)
        print('{} ==> {}_{}_{}.jpg'.format(i, prefix, i, label))

if __name__ == '__main__':
    train_image_file = '/home/hadoop-hch/Downloads/MNIST of handwritten digits/train-images.idx3-ubyte'
    train_label_file = '/home/hadoop-hch/Downloads/MNIST of handwritten digits/train-labels.idx1-ubyte'
    test_image_file = '/home/hadoop-hch/Downloads/MNIST of handwritten digits/t10k-images.idx3-ubyte'
    test_label_file = '/home/hadoop-hch/Downloads/MNIST of handwritten digits/t10k-labels.idx1-ubyte'

    save_train_dir = '/home/hadoop-hch/train_images/'
    save_test_dir = '/home/hadoop-hch/test_images/'

    if not os.path.exists(save_train_dir):
        os.makedirs(save_train_dir)
    if not os.path.exists(save_test_dir):
        os.makedirs(save_test_dir)

    save_mnist_to_jpg(train_image_file, train_label_file, save_train_dir)
    save_mnist_to_jpg(test_image_file, test_label_file, save_test_dir)
