import sys, urllib2, json, random
from random import randint
from settings_local import APPLICATION_ID, REST_API_KEY, MASTER_KEY
from parse_rest.connection import register
from parse_rest.datatypes import Object

register(APPLICATION_ID, REST_API_KEY)

user_count = 1

class _User(Object):
	pass

class CaregiverProfile(Object):
	pass

def randrange_float(start, stop, step):
    return random.randint(0, int((stop - start) / step)) * step + start

if __name__ == '__main__':
	if len(sys.argv) > 1:
		user_count = int(sys.argv[1])

	resp = urllib2.urlopen("http://api.randomuser.me/?results=%d" % user_count).read()
	resp = json.loads(resp)

	for i in range(user_count):
		user = _User()
		result = resp['results'][i]['user']
		# print result

		user.username = result['email']
		user.email = user.username
		user.password = 'password'
		user.firstName = result['name']['first'].capitalize()
		user.lastName = result['name']['last'].capitalize()
		user.gender = 'M' if result['gender'] == 'male' else 'F'
		user.phone = result['cell'].replace('-', '')
		user.postalCode = result['location']['zip']
		user.roles = ['Caregiver']

		user.save()

		userId = user.objectId

		profile = CaregiverProfile()
		profile.userId = user
		profile.rating = randrange_float(1, 5, 0.5)
		profile.yearOfExperience = randint(0, 99)

		a, b = randrange_float(0, 1000, 10), randrange_float(0, 1000, 10)
		profile.wageRangeMin = min(a, b)
		profile.wageRangeMax = max(a, b)
		profile.languages = sorted(random.sample(range(8), randint(1, 5)))
		profile.services = sorted(random.sample(range(9), randint(1, 4)))
		profile.profileImage = result['picture']['large']

		profile.save()

		print "User: %r, Profile: %r" % (user, profile)