from math import factorial
from operator import mul

import numpy as np

def prod(collection):
	"""
	Product of all elements in the collection.
	elements must support multiplication
	"""
	return reduce(mul, collection)


def nCr(n,k):
	"""
	Number of combinations: n choose k.
	"""
	return prod(range(n-k+1,n+1)) / factorial(k)


def game_size(n,s):
	"""
	Number of profiles in a symmetric game with n players and s strategies.
	"""
	return nCr(n+s-1,n)


def profile_repetitions(p):
	"""
	Number of normal form profiles that correspond to a role-symmetric profile.
	"""
	return prod([factorial(sum(row)) / prod(map(factorial, row)) for row in p])


def list_repr(l, sep=", "):
	"""
	Creates a string representation of the elements of a collection.
	"""
	try:
		return reduce(lambda x,y: str(x) + sep + str(y), l)
	except TypeError:
		return ""

def average(l):
	return sum(l, 0.0) / len(l)

tiny = 1e-10

def weighted_least_squares(x, y, weights):
	"appends the ones for you; puts 1D weights into a diagonal matrix"
	try:
		A = np.append(x, np.ones([x.shape[0],1]), axis=1)
		W = np.zeros([x.shape[0]]*2)
		np.fill_diagonal(W, weights)
		return y.T.dot(W).dot(A).dot(np.linalg.inv(A.T.dot(W).dot(A)))
	except np.linalg.linalg.LinAlgError as e:
		z = A.T.dot(W).dot(A)
		for i in range(z.shape[0]):
			for j in range(z.shape[1]):
				z[i,j] += np.random.uniform(-tiny,tiny)
		return y.T.dot(W).dot(A).dot(np.linalg.inv(z))



