from py2neo import Graph, Node, Relationship

graph = Graph()

search_term = "yum.noarch"
limit = "2500"

result = graph.cypher.execute('MATCH n--u WHERE u.name = "' + search_term + '"  MATCH u--n1 WHERE n1.id <> n.id MATCH n1--u1 WHERE u1.name <> "' + search_term + '" AND NOT u1--n RETURN u1 LIMIT ' + limit)

print result
