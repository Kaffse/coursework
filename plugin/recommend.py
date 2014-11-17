from dnfpluginscore import logger

import dnf

def register(packagelist, uid):
    return None

def installed_package(package, uid):
    return None

def uninstalled_package(package, uid):
    return None

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
    summary = _('Makes a recommendation based on your currently installed packaged')

    def update_packages(package, uid):
        return None

    def get_recommend_list(uid):
        return None
