from dnfpluginscore import logger

import dnf
import requests as r
import json
import uuid
import os
import collections
from py2neo import Graph, Node, Relationship

HOME = os.getenv('HOME')
SERVER_ADDRESS = 'http://main.f-t.so:7474/db/data'
THIS_UUID = uuid.uuid1()

class Recommend(dnf.Plugin):

    name = 'recommend'

    def __init__(self, base, cli):
        self.base = base
        self.cli = cli
        global THIS_UUID
        try:
            id_file = open(HOME + "/.recommend_uuid", 'r')
            THIS_UUID = uuid.UUID(id_file.readline())
            id_file.close()
            print "UUID Successfully Read"
        except:
            print "No UUID File. New UUID"

        if self.cli is not None:
            self.cli.register_command(RecommendCommand)

    def get_installed(self):
        query = self.base.sack.query()
        installed = query.installed()
        return list(installed)

    def _out(self, msg):
        logger.debug('Recommend plugin: %s', msg)

    def config(self):
        self._out('config')

    def sack(self):
        self._out('sack')
        self._out(self.get_installed())

    def transaction(self):
        self._out('transaction')

class RecommendCommand(dnf.cli.Command):
    aliases = ['recommend']
    summary = 'Makes a recommendation based on your currently installed packaged'

    def get_recommend_list(self, package):
        graph = Graph()

        search_term = package
        limit = "2500"
        search_string = 'MATCH n--u WHERE u.name = "' + search_term + '" AND n.id = "' + THIS_UUID.hex + '" MATCH u--n1 WHERE n1.id <> n.id MATCH n1--u1 WHERE u1.name <> "' + search_term + '" AND NOT u1--n RETURN u1 LIMIT ' + limit

        result = graph.cypher.execute(search_string)

        popular_counter = collections.Counter(result)

        print popular_counter.most_common(10)

    def make_graph(self, packagelist):
        graph = Graph()

        user_list = graph.merge("User", "id", THIS_UUID.hex)

        id_file = open(HOME + "/.recommend_uuid", 'w+')
        id_file.write(THIS_UUID.hex)
        id_file.close()

        for user in user_list:
            this_pc = user

        for package in packagelist:
            nodes = graph.merge("Package", "name", package.name)
            for node in nodes:
                relationship = Relationship(this_pc, "INSTALLED", node)
                graph.create_unique(relationship)

    def run(self, extcmds):
        """Execute the command."""

        if len(extcmds) > 1:
            print "Invalid Argments for Recommend plugin"
        elif len(extcmds) == 0:
            print("Please add the name of a package or use the 'update' command")
        elif extcmds[0].lower() == "update":
            print("Discovering Installed Packages...")
            self.base.fill_sack()
            packages = self.base.sack.query()
            packages = packages.installed()
            print("Building Graph...")
            self.make_graph(list(packages))
            print("Complete!")
        elif len(extcmds) == 1:
            print("Querying Graph for " + extcmds[0])
            self.get_recommend_list(extcmds[0])
        else:
            print "Invalid Argments for Recommend plugin"
