package org.opwo.gradle


import groovy.util.*
import groovy.io.FileType
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.text.SimpleDateFormat


class Metalink extends DefaultTask {

	String fileSet
	String url
	String outputFile

	@TaskAction
	def meta() {
		if (!url){
			url = project.properties['serverFilesUrl']
		}


		def dir = new File(fileSet)
		def files = []


		dir.eachFileRecurse (FileType.FILES) {file ->
			files << file
		}

		def stringWriter = new StringWriter()
		def metaBuilder = new MarkupBuilder(stringWriter)
		metaBuilder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def date = new Date()
		def sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")


		metaBuilder.metalink('xlmns': 'urn:ietf:params:xml:ns:metalink') {
			published(sdf.format(date))
			files.each {
				def f_file = it
				def f_name = it.name
				def f_path = it.path
				f_path = f_path.replace(fileSet, '')
				f_path = f_path.replace('\\', '/')

				def f_size = it.length()

				file('name': f_name){
					size(f_size)
					hash('type': 'md5', new MDP().getMDP(f_file))
					uri(url+f_path)
				}
			}
		}

		//wyswietl
		def xml = stringWriter.toString()
		//println xml

		//zapisz
		File out_file = new File(outputFile)
		out_file.write xml



	}
}