from dnfpluginscore import logger

import dnf
import requests as r
import json
import uuid
from py2neo import Graph, Node, Relationship

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

def print_recommend(package_list):
    ui_type = '1'
    if ui_type == '1':
        print "Prompt Install"
    elif ui_type == '2':
        print "Auto Install"
    elif ui_type == '3':
        print "List Choice"
    else:
        print "UI Selection Error"

class Recommend(dnf.Plugin):

    name = 'recommend'

    def __init__(self, base, cli):
        self.base = base
        self.cli = cli

        if self.cli is not None:
            self.cli.register_command(RecommendCommand)

    def get_installed(self):
        query = self.base.sack.query()
        installed = query.installed()
        return list(installed)

    def _out(self, msg):
        logger.debug('Recommend plugin: %s', msg)

    #this order holds true when updating/installing packages
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

    def update_packages(self, packagelist, uid):
        data = json.dumps({'packagelist':packagelist, 'uid':uid})
        response = r.post(SERVER_ADDRESS + '/update', data)
        print SERVER_ADDRESS + '/update'
        return response.text

    def get_recommend_list(self, uid):
        data = json.dumps({'uid':uid})
        return r.post(SERVER_ADDRESS + '/recommend', data)

    def make_graph(self, packagelist):
        graph = Graph()

        user_list = graph.merge("User", "id", uuid.uuid1())

        for user in user_list:
         this_pc = user

        for package in packagelist:
            nodes = graph.merge("Package", "name", package.name)
            for node in nodes:
                relationship = Relationship(this_pc, "INSTALLED", node)
                graph.create(relationship)

    def run(self, extcmds):
        """Execute the command."""

        #placeholder for recommend list
        list = [100]

        if len(extcmds) > 1:
            print "Invalid Argments for Recommend plugin"
        elif len(extcmds) == 0:
            packages = self.base.sack.query()
            packages = packages.installed()
            packagelist = list(packages)
            self.make_graph(packagelist)
        elif extcmds[0].lower() == "update":
            print_recommend(self.update_packages(list, 100))
        else:
            print "Invalid Argments for Recommend plugin"

# implement A/B UI
# auto install - prompt based - list


# write working config
# num of reccomends, ip of server


# association rule mining??
# Look into ways to store backend table stuff
# Finish up shiney frontend
# write survey questions on A/B UI
