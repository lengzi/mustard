package org.data2semantics.mustard.kernels.graphkernels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.data2semantics.mustard.kernels.data.RDFData;
import org.data2semantics.mustard.kernels.data.SingleDTGraph;
import org.data2semantics.mustard.learners.SparseVector;
import org.data2semantics.mustard.rdf.RDFDataSet;
import org.data2semantics.mustard.rdf.RDFUtils;
import org.nodes.DTGraph;
import org.nodes.DTLink;
import org.nodes.DTNode;
import org.nodes.MapDTGraph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

public class RDFWLSubTreeKernel implements GraphKernel<RDFData>, FeatureVectorKernel<RDFData> {
	private int depth;
	private String label;
	private boolean inference;
	private RDFDTGraphWLSubTreeKernel kernel;
	private DTGraph<String,String> graph;
	private List<DTNode<String,String>> instanceNodes;

	public RDFWLSubTreeKernel(int iterations, int depth, boolean inference, boolean normalize) {
		this(iterations, depth, inference, false, false, normalize);
	}

	public RDFWLSubTreeKernel(int iterations, int depth, boolean inference, boolean reverse, boolean iterationWeighting, boolean normalize) {
		super();
		this.label = "RDF_WL_Kernel_" + depth + "_" + iterations + "_" + inference + "_" + reverse + "_" + iterationWeighting + "_" + normalize;
		this.depth = depth;
		this.inference = inference;

		kernel = new RDFDTGraphWLSubTreeKernel(iterations, depth, reverse, iterationWeighting, normalize);
	}

	public String getLabel() {
		return label;
	}

	public void setNormalize(boolean normalize) {
		kernel.setNormalize(normalize);
	}

	public SparseVector[] computeFeatureVectors(RDFData data) {
		init(data.getDataset(), data.getInstances(), data.getBlackList());
		return kernel.computeFeatureVectors(new SingleDTGraph(graph, instanceNodes));
	}

	public double[][] compute(RDFData data) {
		init(data.getDataset(), data.getInstances(), data.getBlackList());
		return kernel.compute(new SingleDTGraph(graph, instanceNodes));
	}

	private void init(RDFDataSet dataset, List<Resource> instances, List<Statement> blackList) {
		Set<Statement> stmts = RDFUtils.getStatements4Depth(dataset, instances, depth, inference);
		stmts.removeAll(blackList);
		graph = RDFUtils.statements2Graph(stmts, RDFUtils.REGULAR_LITERALS);
		instanceNodes = RDFUtils.findInstances(graph, instances);
		graph = RDFUtils.simplifyInstanceNodeLabels(graph, instanceNodes);
	}	
}
