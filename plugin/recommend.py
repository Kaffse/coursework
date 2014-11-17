from dnfpluginscore import logger

import dnf


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
