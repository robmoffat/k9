package com.kite9.k9server.docker;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.arquillian.cube.docker.impl.util.DockerMachine;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Filters;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig.DockerClientConfigBuilder;

public class DockerInspector {

	public static String getImageRunningContainerIPAddress(String image) {
		DockerClient client = null;
		if (!System.getenv().containsKey("DOCKER_HOST")) {
			throw new RuntimeException("The following must be set in the environment: " + "DOCKER_HOST, DOCKER_MACHINE_NAME, DOCKER_TLS_VERIFY, DOCKER_CERT_PATH");
		} else {
			// this will use environment variables
			client = DockerClientBuilder.getInstance().build();
		}

		Info info = client.infoCmd().exec();
		System.out.println(info);

		ListContainersCmd list = client.listContainersCmd();
		Filters f = new Filters();
		f.withImages(image);
		list.withFilters(f);
		List<Container> containers = list.exec();

		// there should be just one.
		if (containers.size() != 1) {
			throw new RuntimeException("There are " + containers.size() + " containers running with image " + image);
		}

		String containerId = containers.get(0).getId();

		InspectContainerResponse response = client.inspectContainerCmd(containerId).exec();
		return response.getNetworkSettings().getIpAddress();

	}

	public static String getIPAddressOfNamedContainer(String name) {
		DockerClient client = DockerClientBuilder.getInstance().build();
		InspectContainerResponse response = client.inspectContainerCmd(name).exec();
		return response.getNetworkSettings().getIpAddress();
	}
}
