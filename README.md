# Application of Conformal Geometric Algebra to In-plane Rotated Face Detection by AdaBoost-based Algorithm

## NetBean project:

	- 20180228_gauss_yi: implementation of AdaBoost with Gaussian distribution
		- Outputs:
			- 20180228_gauss.wk (for Frontal Face Detection)
			- 20180321_mv_gauss.wk (for Multi-view Face Detection)

	- 20180228_vj_yi: implementation of AdaBoost by Viola-Jones method
		- Outputs:
			- 20180301_vj.wk
			- 20180321_mv_vj.wk

	- 20180320_cga_1_yi: implementation of AdaBoost with CGA 1-D
		Outputs:
			- 20180320_cga_1.wk
			- 20180321_mv_cga_1.wk

	- 20180320_cga_2_yi: implementation of AdaBoost with CGA 2-D
		Outputs:
			- 20180322_cga_2.wk
			- 20180321_mv_cga_2.wk

	- 20180320_cga_3_yi: implementation of AdaBoost with CGA 3-D
		Outputs:
			- 20180322_cga_3.wk
			- 20180322_mv_cga_3.wk

	For each project: frontal and MV Face Detection are involved. Just change dataset to train each model
	Each Strongclass (.wk file) mentioned above consists of 200 Weakclass 

--------------------------------------------------------------------------------

## Program structure:

	main
	|__ trainProcess
	|__ testProcess


### Training Process:
	trainProcess
	|__ readInputImage
	|__ prepareIntegralImage
	|__ prepareFeatureIndex
	|__ algorithmAdaboost
	|__ saveArrWeakClass

#### Functions:

	ArrayList<int[][]> readInputImage(String path)
		Parameter:
			- path: directory path to get input images
		Usage:
			- Read and pre-process input image
		Step:
			- Read images from given path
			- Convert to Grayscale
			- Normalize image
			- Return an ArrayList


	void prepareIntegralImage()
		Usage: 
			- Compute integral images
		Step:
			- Get Integral images and store in an ArrayList


	void prepareFeatureIndex()
		Usage:
			- Read information of Haar-like pattern
		Step:
			- Read Color map
			- Read Feature info from Lookup table
			(color map and lookup table are stored in .txt file at specific path in project)

	WeakClass[] algorithmAdaboost()
		Usage:
			- Train Strongclass from given Integral images and Haar-like pattern
			- Each WeakClass is chosen from 5000 random features
		Step:
			- Initialize Weight for Face and Non-face samples
			- For each round:
				- Normalize Weights
				- Get Feature with smallest error
				- Calculate Alpha
				- Update Weights

	Feature getFeatureIndex(double[] w_p, double[] w_n)
		Parameter:
			- w_p: Weights of Face
			- w_n: Weights of Non-face
		Usage:
			- Choose the Feature that yields the smallest error rate with given Weights for Face and Non-face
		Step:
			- Get K (=5000) random features
			- For each feature:
				- Construct CGA cluster for Face and Non-face
				- Find distance from each sample to each region to classify
				- Compute error to choose feature producing least error rate

	double[] getFeatureValue(ArrayList<int[][]> lstInt, int idx)
		Parameter:
			- lstInt: list of Integral images
			- idx: index of Haar-like feature
		Usage:
			- Compute feature value for the whole set of image with given feature index
		Step:
			- Get information of Haar-like feature
			- Compute feature value by applying Color map read before

	double getFeatureValueSingle(int[][] imgInt, int idx)
		Parameter:
			- imgInt: a single Integral image
			- idx: index of Haar-like feature
		Usage:
			- Compute feature value of a single image with given feature index
		Step:
			- Apply Color map to compute feature value


### Test Process:
	testProcess
	|__ readInputImage
	|__ prepareIntegralImage
	|__ prepareFeatureIndex
	|__ loadWeakClass
	|__ testAdaBoost

#### Functions:

	void testAdaBoost(WeakClass[] arrWeakClass, int len, double rate)
		Parameter:
			- arrWeakClass: StrongClass
			- len: the number of WeakClass used for testing
			- rate: rate used for testing (instead of using 0.5 as in origin AdaBoost algorihtm)
		Usage:
			- Test StrongClass with given number of WeakClass and rate to find TPR, FPR
		Step:
			- For each test sample:
				- Construct vector CGA_x
				- Find distance to each CGA cluster then compare to classify
			- Calculate TPR and FPR

	double findGaussDist(double[] x, double x_mag2, double[][] s, double[] lambda, double[] s_inf, double[] s_0)
		Parameter:
			- x: vector CGA x
			- x_mag2: magnitude of x
			- other: eigen-values, eigen-vectors of corresponding CGA cluster
		Usage:
			- Calculate the distance to the CGA cluster
		Step:
			- Just apply formula in CGA paper