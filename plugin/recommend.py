from dnfpluginscore import logger

import dnf
import requests as r
import json

SERVER_ADDRESS = 'http://127.0.0.1'

def register(packagelist, uid):
    data = json.dumps({'packagelist':packagelist, 'uid':uid})
    return r.post(SERVER_ADDRESS + '/register', data)

def installed_package(package, uid):
    data = json.dumps({'package':package, 'uid':uid})
    return r.post(SERVER_ADDRESS + '/install', data)

def uninstalled_package(package, uid):
    data = json.dumps({'package':package, 'uid':uid})
    return r.post(SERVER_ADDRESS + '/uninstall', data)

class Recommend(dnf.Plugin):

    name = 'recommend'

    def __init__(self, base, cli):
        self.base = base
        self.cli = cli
        if self.cli is not None:
            self.cli.register_command(RecommendCommand)

#    def _out(self, msg):
#        logger.debug('Recommend plugin: %s', msg)
#
#    def config(self):
#        self._out('config')
#
#    def sack(self):
#        self._out('sack')
#
#    def transaction(self):
#        self._out('transaction')

class RecommendCommand(dnf.cli.Command):
    aliases = ['recommend']
    summary = 'Makes a recommendation based on your currently installed packaged'

    def update_packages(self, packagelist, uid):
        data = json.dumps({'packagelist':packagelist, 'uid':uid})
        response =  r.post(SERVER_ADDRESS + '/update', data)
        return response.text

    def get_recommend_list(uid):
        data = json.dumps({'uid':uid})
        return r.post(SERVER_ADDRESS + '/recommend', data)

    def run(self, extcmds):
        """Execute the command."""

        list = [100]
        print(self.update_packages(list ,100))
