from dnfpluginscore import logger

import dnf


class Recommend(dnf.Plugin):

    name = 'recommend'

    def __init__(self, base, cli):
        self.base = base
        self.cli = cli
        if cli is None:
            self._out('loaded.')
        else:
            self._out('loaded (with CLI)')

    def _out(self, msg):
        logger.debug('Recommend plugin: %s', msg)

    def config(self):
        self._out('config')

    def sack(self):
        self._out('sack')

    def transaction(self):
        self._out('transaction')
